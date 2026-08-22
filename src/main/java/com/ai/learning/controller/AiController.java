package com.ai.learning.controller;


import com.ai.learning.VO.AiExplainVO;
import com.ai.learning.common.Result;
import com.ai.learning.dto.AiExplainDTO;
import com.ai.learning.entity.Question;
import com.ai.learning.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "AI 生成题目")
    @PostMapping("/generate-question")
    public Result<Question> generateQuestion(
            @Parameter(description = "知识点分类") @RequestParam String category,
            @Parameter(description = "题型： 1单选 2多选 3判断") @RequestParam(defaultValue = "1") Integer type
    ){
        return Result.success(aiService.generateQuestion(category,type));
    }
}
