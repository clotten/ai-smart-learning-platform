package com.ai.learning.user.controller;

import com.ai.learning.common.Result;
import com.ai.learning.user.dto.ProfileDTO;
import com.ai.learning.user.entity.SysUser;
import com.ai.learning.user.service.UserService;
import com.ai.learning.user.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户接口（需要登录才能访问，验证拦截器用）
 */
@RestController
@RequestMapping("/api/user")
public class UserController{
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "返回用户数据")
    @GetMapping("/info")
    public Result<Map<String,Object>> info(HttpServletRequest request){
        Map<String,Object> data = new HashMap<>();
        data.put("userId",request.getAttribute("userId"));
        data.put("username",request.getAttribute("username"));
        data.put("role",request.getAttribute("role"));
        return Result.success(data);
    }

    @Operation(summary = "获取当前用户完整信息")
    @GetMapping("/me")
    public Result<UserVO> me(HttpServletRequest request){
        Object userId = request.getAttribute("userId");
        SysUser user = userService.findById(Long.valueOf(userId.toString()));
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return Result.success(vo);
    }


    @Operation(summary = "更新个人信息（昵称/简介）")
    @PutMapping("/profile")
    public Result<UserVO> uodateProfile(@RequestBody @Valid ProfileDTO dto, HttpServletRequest request){
        Object userId = request.getAttribute("userId");
        return Result.success(userService.updateProfile(Long.valueOf(userId.toString()), dto));
    }
}

