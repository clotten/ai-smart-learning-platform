package com.ai.learning.ai.controller;


import com.ai.learning.ai.vo.AiExplainVO;
import com.ai.learning.common.Result;
import com.ai.learning.ai.dto.AiExplainDTO;
import com.ai.learning.ai.dto.ChatDTO;
import com.ai.learning.question.entity.Question;
import com.ai.learning.ai.service.AiService;
import com.ai.learning.common.service.RateLimitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.Executor;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    //流式调用耗时，使用线程池异步执行（不阻塞Tomcat）
    private final Executor aiExecutor;
    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "AI 讲解错题")
    @PostMapping("/explain")
    public Result<AiExplainVO> explain(@RequestBody @Valid AiExplainDTO dto, HttpServletRequest request){
        Object userId = request.getAttribute("userId");
        return Result.success(aiService.explainWrongQuestion(dto,Long.valueOf(userId.toString())));
    }

    @Operation(summary = "AI 生成题目")
    @PostMapping("/generate-question")
    public Result<Question> generateQuestion(
            @Parameter(description = "知识点分类") @RequestParam String category,
            @Parameter(description = "题型： 1单选 2多选 3判断") @RequestParam(defaultValue = "1") Integer type
    ){
        return Result.success(aiService.generateQuestion(category,type));
    }

    @Operation(summary = "AI 讲解错题（流式，打字机效果）")
    @GetMapping(value = "/explain-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter explainStream(
            @Parameter(description = "题目id") @RequestParam Long questionId,
            @Parameter(description = "你的答案") @RequestParam String userAnswer,
            HttpServletRequest request
    ){
        Object userId = request.getAttribute("userId");
        SseEmitter emitter = new SseEmitter(120_000L); //2分钟超时

        //异步执行，避免流式等待阻塞 Tomcat 线程
        aiExecutor.execute(() -> {
            try{
                AiExplainDTO dto = new AiExplainDTO();
                dto.setQuestionId(questionId);
                dto.setUserAnswer(userAnswer);
                aiService.explainStream(dto, Long.valueOf(userId.toString()), emitter);
            } catch (Exception e) {
                log.error("AI流式错题解析任务异常");
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    @Operation(summary = "AI 自由对话（流式）")
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(
            @Parameter(description = "用户消息") @RequestBody ChatDTO dto,
            HttpServletRequest request
    ){
        SseEmitter emitter = new SseEmitter(120_000L);
        // 从 token 拿用户身份
        Object userId = request.getAttribute("userId");
        //Ai 限流：每个用户一分钟最多5次
        if(!rateLimitService.tryLimit("ai",Long.valueOf(userId.toString()))){
            try{
                emitter.send(objectMapper.writeValueAsString(Map.of("error","AI调用太频繁，请稍后再试")));
            } catch (Exception e) {
                log.error("推送错误事件失败",e);
            }
            emitter.complete();
            return emitter;
        }
        aiExecutor.execute(
                () -> {
                    try{
                        aiService.chatStream(dto.getMessage(),emitter);
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                }
        );
        return emitter;
    }
}
