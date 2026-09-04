package com.ai.learning.question.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新增题目请求体：只接收业务字段
 * id / createdBy / createdAt / updatedAt /deleted 由服务器决定，这里不收！
 */
@Data
public class QuestionCreateDTO {

    @NotNull(message = "题型不能为空")
    private Integer type;

    @NotNull(message = "分类不能为空")
    private String category;

    @NotNull(message = "题干不能为空")
    private String content;

    private String options;

    @NotNull(message = "答案不能为空")
    private String answer;

    private String analysis;

    private Integer difficulty;
}
