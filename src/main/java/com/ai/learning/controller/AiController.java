package com.ai.learning.controller;


import com.ai.learning.VO.AiExplainVO;
import com.ai.learning.common.Result;
import com.ai.learning.dto.AiExplainDTO;
import com.ai.learning.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    @Operation(summary = "AI 讲解错题")
    @PostMapping("/explain")
    public Result<AiExplainVO> explain(@RequestBody @Valid AiExplainDTO dto, HttpServletRequest request){
        Object userId = request.getAttribute("userId");
        return Result.success(aiService.explainWrongQuestion(dto,Long.valueOf(userId.toString())));
    }
}
