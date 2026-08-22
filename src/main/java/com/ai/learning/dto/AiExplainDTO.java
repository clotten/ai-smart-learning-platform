package com.ai.learning.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AiExplainDTO {

    @NotNull(message = "题目id不能为空")
    private Long questionId;

    @NotNull(message = "请传入你的答案")
    private String userAnswer;
}
