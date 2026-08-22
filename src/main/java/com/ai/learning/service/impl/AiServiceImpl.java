package com.ai.learning.service.impl;


import com.ai.learning.VO.AiExplainVO;
import com.ai.learning.common.BusinessException;
import com.ai.learning.dto.AiExplainDTO;
import com.ai.learning.entity.Question;
import com.ai.learning.mapper.QuestionMapper;
import com.ai.learning.service.AiService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final QuestionMapper questionMapper;

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
            Object messageObj = ((Map<?, ?>)choices.get(0)).get("message");
            return messageObj == null ? "" : messageObj.toString();
        } catch (Exception e) {
            throw new BusinessException("AI 服务调用失败：" + e.getMessage());
        }
    }
}
