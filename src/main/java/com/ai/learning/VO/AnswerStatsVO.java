package com.ai.learning.VO;


import lombok.Data;

/**
 * 学习统计VO
 */
@Data
public class AnswerStatsVO {

    private Long totalCount;    //总刷题数

    private Long correctCount;  //答对数

    private Double accuracy;    //正确率（百分比，如66.7）
}
