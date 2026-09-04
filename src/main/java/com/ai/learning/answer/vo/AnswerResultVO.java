package com.ai.learning.answer.vo;


import lombok.Data;

/**
 * 答题结果VO：返回给前端“对错 + 解析”
 * 注意：不返回完整Question
 */
@Data
public class AnswerResultVO {

    private Long questionId;        //题目id

    private String content;         //题干

    private String userAnswer;      //用户提交的答案

    private String correctAnswer;   //正确答案

    private Boolean correct;        //是否答对

    private String analysis;        //解析（答对答错都返回，方便学习）
}
