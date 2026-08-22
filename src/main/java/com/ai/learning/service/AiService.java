package com.ai.learning.service;

import com.ai.learning.VO.AiExplainVO;
import com.ai.learning.dto.AiExplainDTO;

public interface AiService {
    /**
     * AI讲解错题
     */
    AiExplainVO explainWrongQuestion(AiExplainDTO dto, Long userId);
}
