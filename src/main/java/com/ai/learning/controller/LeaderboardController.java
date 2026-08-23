package com.ai.learning.controller;


import com.ai.learning.VO.LeaderboardVO;
import com.ai.learning.common.Result;
import com.ai.learning.service.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @Operation(summary = "刷题排行榜 TOP10")
    @GetMapping("/top")
    public Result<List<LeaderboardVO>> top(
            @Parameter(description = "取前几名") @RequestParam(defaultValue = "10") int n){
        return Result.success(leaderboardService.top(n));
    }

    @Operation(summary = "我的刷题排名")
    @GetMapping("/me")
    public Result<LeaderboardVO> myRank(HttpServletRequest request){
        Object userId = request.getAttribute("userId");
        return Result.success(leaderboardService.myRank(Long.valueOf(userId.toString())));
    }

}
