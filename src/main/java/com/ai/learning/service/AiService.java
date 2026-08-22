package com.ai.learning.service;

import com.ai.learning.VO.AiExplainVO;
import com.ai.learning.dto.AiExplainDTO;
import com.ai.learning.entity.Question;

public interface AiService {
    /**
     * AI讲解错题
     */
    AiExplainVO explainWrongQuestion(AiExplainDTO dto, Long userId);

    /**
     * AI生成题目
     */
    Question generateQuestion(String category, Integer type);
}
