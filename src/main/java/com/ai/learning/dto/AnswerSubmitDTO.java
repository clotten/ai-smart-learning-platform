package com.ai.learning.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 提交答案请求体
 */
@Data
public class AnswerSubmitDTO {

    @NotNull(message = "题目id不能为空")
    private Long questionId;

    @NotNull(message = "答案不能为空")
    private String userAnswer;

}
