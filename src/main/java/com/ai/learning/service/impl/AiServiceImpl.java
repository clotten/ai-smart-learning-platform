package com.ai.learning.service.impl;


import com.ai.learning.VO.AiExplainVO;
import com.ai.learning.common.BusinessException;
import com.ai.learning.dto.AiExplainDTO;
import com.ai.learning.entity.Question;
import com.ai.learning.mapper.QuestionMapper;
import com.ai.learning.service.AiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final QuestionMapper questionMapper;

    private final ObjectMapper objectMapper;

    //配置注入（key 在 application-local.yml）
    @Value("${app.deepseek.api-key}")
    private String apiKey;

    @Value("${app.deepseek.base-url}")
    private String baseUrl;

    @Value("${app.deepseek.model}")
    private String model;

    //Spring 6 的RestClient：简洁的HTTP客户端
    private RestClient restClient;

    @PostConstruct
    public void init(){     // Spring 初始化完这个 Bean 后自动调用
        restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION,"Bearer " + apiKey)
                .build();
    }
    @Override
    public AiExplainVO explainWrongQuestion(AiExplainDTO dto, Long userId){
        //1.查题目
        Question question = questionMapper.selectById(dto.getQuestionId());
        if(question == null){
            throw new BusinessException("题目不存在");
        }

        //2.设计 Prompt （角色 + 任务 + 约束）
        String prompt = """
                你是一位耐心的编程辅导老师。
                                请帮学生讲解这道错题，要求：
                                1. 先给出正确答案和原因
                                2. 解释学生的答案为什么错
                                3. 补充相关核心知识点
                                4. 简洁易懂，200字以内
                
                                题目：%s
                                正确答案：%s
                                学生答案：%s
                """.formatted(question.getContent(),question.getAnswer(),dto.getUserAnswer());

        //3.调 DeepSeek API
        String aiExplain = callDeepSeek(prompt);

        //4.组装 VO 返回
        AiExplainVO vo = new AiExplainVO();
        vo.setQuestionId(question.getId());
        vo.setContent(question.getContent());
        vo.setMyAnswer(dto.getUserAnswer());
        vo.setCorrectAnswer(question.getAnswer());
        vo.setAiExplain(aiExplain);
        return vo;
    }

    @Override
    public Question generateQuestion(String category, Integer type){
        //1.设计Prompt：明确要求只输出JSON
        String prompt = """
                请生成一道%s题目，要求：
                            1. 题型：%s
                            2. 只输出一个 JSON 对象，不要任何其他文字
                            3. JSON 格式必须是：{"type":%d,"category":"%s","content":"题干","options":"选项JSON","answer":"正确答案","analysis":"解析","difficulty":1-5(取整数)}
                            4. options 格式：{"A":"选项A","B":"选项B","C":"选项C","D":"选项D"}，判断题 options 为 null
                            5. 答案必须真实正确
                """.formatted(category,type == 1 ? "单选题" : (type == 2 ? "多选题" : "判断题"), type, category);

        //2.调AI
        String aiJson = callDeepSeek(prompt);

        //3.解析JSON -> Question （AI可能加```json```代码块，先清理）
        try{
            String clean = aiJson.replace("```json","").replace("```","").trim();
            JsonNode node = objectMapper.readTree(clean);

            Question q = new Question();
            q.setType(node.get("type").asInt());
            q.setCategory(node.get("category").asText());
            q.setContent(node.get("content").asText());
            //options 统一处理：字符串直接用，对象转成JSON字符串
            JsonNode optionsNode = node.get("options");
            if(optionsNode != null && !optionsNode.isNull()){
                q.setOptions(optionsNode.isTextual() ? optionsNode.asText() : optionsNode.toString());
            }
            q.setAnswer(node.get("answer").asText());
            q.setAnalysis(node.get("analysis").asText());
            q.setDifficulty(node.get("difficulty").asInt());
            return q;
        } catch (Exception e) {
            log.info("AI 原始返回:{}", aiJson);
            throw new BusinessException("AI 生成的题目格式有误，请重试");
        }
    }
    /**
     * 调用 DeepSeek 聊天接口 （OpenAI 兼容格式）
     */
    private String callDeepSeek(String prompt){
        try{
            //请求体（OpenAi兼容）
            Map<String, Object> body = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role","system","content","你是专业的编程辅导老师"),
                            Map.of("role","user","content",prompt)
                    ),
                    "temperature", 0.7
            );

            //发请求
            Map<?,?> response = restClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            if(response == null){
                throw new BusinessException("AI 返回异常");
            }
            //解析响应：choices[0].message.content
            Object choiceObj = response.get("choices");
            if(!(choiceObj instanceof List<?> choices) || choices.isEmpty()){
                throw new BusinessException("AI 返回异常");
            }
            if(choices.get(0) instanceof Map<?, ?> firstChoice
                    && firstChoice.get("message") instanceof Map<?, ?> messageMap) {
                Object contentObj = messageMap.get("content");

                if (contentObj instanceof String content && !content.isBlank()) {
                    return content;
                }
            }
            throw new BusinessException("AI 返回异常");
        } catch (Exception e) {
            throw new BusinessException("AI 服务调用失败：" + e.getMessage());
        }
    }

    @Override
    public void explainStream(AiExplainDTO dto, Long userId, SseEmitter emitter){
        Question question = questionMapper.selectById(dto.getQuestionId());
        if(question == null){
            throw new BusinessException("题目不存在");
        }

        String prompt= """
                你是一位耐心的编程辅导老师。
                            请帮学生讲解这道错题，要求简洁易懂，200字以内。
                
                            题目：%s
                            正确答案：%s
                            学生答案：%s
                """.formatted(question.getContent(),question.getAnswer(),dto.getUserAnswer());

        try{
            //请求体加 stream：true —— 开启流式
            Map<String, Object> body = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role","system","content","你是专业的编程辅导老师"),
                            Map.of("role","user","content",prompt)
                    ),
                    "temperature", 0.7,
                    "stream", true
            );

            //流式读取DeepSeek响应，边读边推给前端
            restClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .exchange((request,response) ->{
                        //逐行读取SSE数据流  try-with-resources:读完自动关闭BufferedReader（连带低层流）
                        try(BufferedReader reader = new BufferedReader(
                                new InputStreamReader(response.getBody(), StandardCharsets.UTF_8)
                        )) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (!line.startsWith("data:")) continue; //只处理 data: 行
                                String data = line.substring(5).trim();
                                if ("[DONE]".equals(data)) break;    //结束标记

                                //取delta.content(每一小段)
                                JsonNode node = objectMapper.readTree(data);
                                JsonNode choicesNode = node.path("choices");
                                //choices为空直接跳过
                                if(choicesNode.isEmpty()) continue;
                                JsonNode delta = choicesNode.path(0).path("delta").path("content");
                                if (delta.isTextual() && !delta.asText().isEmpty()) {
                                    //包装成json使得apiFox能够自动合并
                                    Map<String, String> wrap = Map.of("content", delta.asText());
                                    emitter.send(objectMapper.writeValueAsString(wrap)); //推给前端,自动转义引号/换行
                                }
                            }
                            emitter.complete(); //全部推完
                        } catch (Exception e) {
                            emitter.completeWithError(e); //出错告诉前端
                        }
                        return null;
                    });
        } catch (Exception e) {
            emitter.completeWithError(e);   //出错告诉前端
        }
    }
}
