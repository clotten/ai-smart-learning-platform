package com.ai.learning.controller;

import com.ai.learning.VO.AnswerResultVO;
import com.ai.learning.VO.AnswerStatsVO;
import com.ai.learning.VO.WrongQuestionVO;
import com.ai.learning.common.Result;
import com.ai.learning.dto.AnswerSubmitDTO;
import com.ai.learning.service.AnswerService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/answer")
public class AnswerController {

    private  final AnswerService answerService;

    @Operation(summary =  "提交答案：判分并返回结果")
    @PostMapping("/submit")
    public Result<AnswerResultVO> submit(
            @RequestBody @Valid AnswerSubmitDTO dto,
            HttpServletRequest request){
        return Result.success(answerService.submit(dto,getUserId(request)));
    }

    @Operation(summary = "错题本（分页）")
    @GetMapping("/wrong-list")
    public Result<IPage<WrongQuestionVO>> wrongList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request){
        return Result.success(answerService.wrongList(getUserId(request),pageNum,pageSize));
    }

    @Operation(summary = "学习统计")
    @GetMapping("/stats")
    public Result<AnswerStatsVO> stats(HttpServletRequest request){
        return Result.success(answerService.stats(getUserId(request)));
    }

    /**
     * 从拦截器存的 request attribute 取当前用户的id
     */
    private Long getUserId(HttpServletRequest request){
        Object userId = request.getAttribute("userId");
        return userId == null ? null : Long.valueOf(userId.toString());
    }



}
