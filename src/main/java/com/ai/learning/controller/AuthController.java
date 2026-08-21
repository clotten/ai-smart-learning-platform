package com.ai.learning.controller;

import com.ai.learning.common.Result;
import com.ai.learning.entity.SysUser;
import com.ai.learning.service.UserService;
import com.ai.learning.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证接口：注册、登录
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 注册
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid SysUser user){
        userService.register(user);
        return Result.success();
    }

    /**
     * 登录：成功返回token + 用户信息
     */
    @Operation(summary = "用户登录，返回token")
    @PostMapping("/login")
    public Result<Map<String,Object>> login(@RequestBody Map<String,String> params){
        String username = params.get("username");
        String password = params.get("password");

        //1.校验用户（查库+Bcrypto比对）
        SysUser user = userService.login(username,password);

        //2.登陆成功 + 签发token
        String token = jwtUtil.generateToken(user.getId(),user.getUsername(),user.getRole());

        //3.返回token + 用户信息
        Map<String,Object> data = new HashMap<>();
        data.put("token",token);
        data.put("user",user);

        return Result.success(data);
    }

}
