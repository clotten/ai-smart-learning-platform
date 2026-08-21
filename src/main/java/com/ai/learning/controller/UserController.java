package com.ai.learning.controller;

import com.ai.learning.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户接口（需要登录才能访问，验证拦截器用）
 */
@RestController
@RequestMapping("/api/user")
public class UserController{
    @Operation(summary = "返回用户数据")
    @GetMapping("/info")
    public Result<Map<String,Object>> info(HttpServletRequest request){
        Map<String,Object> data = new HashMap<>();
        data.put("userId",request.getAttribute("userId"));
        data.put("username",request.getAttribute("username"));
        data.put("role",request.getAttribute("role"));
        return Result.success(data);
    }
}

