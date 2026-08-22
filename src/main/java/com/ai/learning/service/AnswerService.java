package com.ai.learning.service;

import com.ai.learning.VO.AnswerResultVO;
import com.ai.learning.VO.AnswerStatsVO;
import com.ai.learning.VO.WrongQuestionVO;
import com.ai.learning.dto.AnswerSubmitDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface AnswerService {

    /**
     * 提交答案：判分+存记录（事务）
     */
    AnswerResultVO submit(AnswerSubmitDTO dto,Long userId);

    /**
     * 错题本：每道题最新记录为错的
     */
    IPage<WrongQuestionVO> wrongList(Long userId,int pageNum,int pageSize);

    /**
     * 学习统计
     */
    AnswerStatsVO stats(Long userId);
}
