package com.ai.learning.ai.service;

import com.ai.learning.ai.vo.AiExplainVO;
import com.ai.learning.ai.dto.AiExplainDTO;
import com.ai.learning.question.entity.Question;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AiService {
    /**
     * AI讲解错题
     */
    AiExplainVO explainWrongQuestion(AiExplainDTO dto, Long userId);

    /**
     * AI生成题目
     */
    Question generateQuestion(String category, Integer type);

    /**
     * AI 讲解错题（流式 ：边生成边推给前端）
     */
    void explainStream(AiExplainDTO dto, Long userId, SseEmitter emitter);

    /**
     * AI 自由对话（流式）
     */
    void chatStream(String userMessage, SseEmitter emitter);
}
