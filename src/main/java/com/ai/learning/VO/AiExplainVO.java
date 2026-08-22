package com.ai.learning.VO;


import lombok.Data;

@Data
public class AiExplainVO {

    private Long questionId;
    private String content;         //题干
    private String myAnswer;        //我答的
    private String correctAnswer;   //正确答案
    private String aiExplain;       //AI讲解
}
