package com.ai.learning.VO;


import lombok.Data;

/**
 * 错题VO：题目 + 我答的 +正确答案 +解析
 */
@Data
public class WrongQuestionVO {

    private Long questionId;

    private String content;         //题干

    private String myAnswer;        //我提交的答案

    private String correctAnswer;   //正确答案

    private String analysis;        //解析

}
