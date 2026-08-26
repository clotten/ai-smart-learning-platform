package com.ai.learning.controller;

import com.ai.learning.VO.LoginVO;
import com.ai.learning.common.Result;
import com.ai.learning.dto.*;
import com.ai.learning.entity.SysUser;
import com.ai.learning.service.GitHubOAuthService;
import com.ai.learning.service.UserService;
import com.ai.learning.service.VerifyCodeService;
import com.ai.learning.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证接口：注册、登录
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    private final JwtUtil jwtUtil;

    private final VerifyCodeService verifyCodeService;
    private final GitHubOAuthService gitHubOAuthService;

    @Value("${app.frontend-url}")
    private String frontendUrl;
    /**
     * 注册
     */
    @Operation(summary = "邮箱注册")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid RegisterDTO dto){
        userService.register(dto);
        return Result.success();
    }

    /**
     * 登录：成功返回token + 用户信息
     */
    @Operation(summary = "邮箱密码登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO dto){

        //1.校验用户（查库+Bcrypto比对）
        SysUser user = userService.login(dto);

        //2.登陆成功 + 签发token
        String token = jwtUtil.generateToken(user.getId(),user.getUsername(),user.getRole());

        //3.组装VO（没有password字段，天然不泄露）+token
        LoginVO vo = new LoginVO();
        BeanUtils.copyProperties(user,vo);
        vo.setToken(token);

        return Result.success(vo);
    }

    @Operation(summary = "发送邮箱验证码")
    @PostMapping("/send-code")
    public Result<Void> sendCode(@RequestBody @Valid SendCodeDTO dto){
        verifyCodeService.send(dto.getEmail());
        return Result.success();
    }

    @Operation(summary = "邮箱验证码登录（未注册自动注册）")
    @PostMapping("/login-by-code")
    public Result<LoginVO> loginByCode(@RequestBody @Valid LoginByCodeDTO dto){
        //1.校验验证码（用后即焚）
        verifyCodeService.verify(dto.getEmail(), dto.getCode());
        //2.查用户/自动注册
        SysUser user = userService.loginByEmail(dto.getEmail());
        //3.签发JWT
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        LoginVO vo = new LoginVO();
        BeanUtils.copyProperties(user, vo);
        vo.setToken(token);
        return Result.success(vo);
    }

    @Operation(summary = "Github登录（跳转授权页）")
    @GetMapping("/github/login")
    public void githubLogin(HttpServletResponse response) throws IOException{
        response.sendRedirect(gitHubOAuthService.buildAuthorizeUrl());
    }

    @Operation(summary = "Github登录回调")
    @GetMapping("/github/callback")
    public void githubCallback(@RequestParam String code, HttpServletResponse response) throws IOException{
        //1.code换access_token
        String accessToken = gitHubOAuthService.getAccessToken(code);
        //2.token拿用户信息
        Map<String, Object> ghUser = gitHubOAuthService.getUserInfo(accessToken);
        Long githubId = ((Number) ghUser.get("id")).longValue();
        String githubLogin = (String) ghUser.get("login");
        //3.查/建用户
        SysUser user = userService.loginByGithub(githubId, githubLogin);
        //4.签发我们自己的 JWT
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        //5.重定向回前端（带token）
        response.sendRedirect(frontendUrl + "/login?token=" + token
        + "&username=" + URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8));
    }

    @Operation(summary = "设置/重置密码（邮箱+验证码）")
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody @Valid ResetPasswordDTO dto){
        userService.resetPassword(dto);
        return Result.success();
    }

}
