package com.ai.learning.ai.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AI 对话请求流
 */
@Data
public class ChatDTO {

    @NotBlank(message = "消息不能为空")
    private String message;
}
