package com.ai.learning.question.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改题目请求体：id 必填（告诉后端改哪条）+业务字段
 */
@Data
public class QuestionUpdateDTO {

    @NotNull(message = "题目id不能为空")
    private Long id;

    @NotNull(message = "题型不能为空")
    private Integer type;

    private String category;

    private String content;

    private String options;

    private String answer;

    private String analysis;

    private Integer difficulty;
}
