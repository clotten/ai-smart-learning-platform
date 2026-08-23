package com.ai.learning.controller;


import com.ai.learning.VO.AiExplainVO;
import com.ai.learning.common.Result;
import com.ai.learning.dto.AiExplainDTO;
import com.ai.learning.entity.Question;
import com.ai.learning.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.Executor;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    //流式调用耗时，使用线程池异步执行（不阻塞Tomcat）
    private final Executor aiExecutor;

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
}
