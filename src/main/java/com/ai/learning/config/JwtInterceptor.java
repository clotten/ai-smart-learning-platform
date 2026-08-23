package com.ai.learning.config;

import com.ai.learning.common.BusinessException;
import com.ai.learning.service.RateLimitService;
import  com.ai.learning.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 拦截器：请求到达Controller之前先验证token
 */
@Component
public class JwtInterceptor implements HandlerInterceptor{

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RateLimitService rateLimitService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @Nullable Object handler){
        //跨域预检请求直接放行（浏览器先发OPTIONS试探）
        if("OPTIONS".equals(request.getMethod())){
            return true;
        }
        //1.从请求头拿token
        String token = request.getHeader("Authorization");
        if(token == null || token.isEmpty()){
            throw new BusinessException("未登录，请先登录");
        }
        //兼容标准格式“Bearer xxx”:去掉前缀再解析
        if(token.startsWith("Bearer ")){
            token = token.substring(7);//“Bearer ”正好7个字符
        }
        //2.解析token（过期/被篡改会抛异常->全局异常处理器接住）
        Claims claims = jwtUtil.parseToken(token);
        //3.把用户信息放进request，后面的Controller直接取用
        request.setAttribute("userId",claims.getSubject());
        request.setAttribute("username",claims.get("username"));
        request.setAttribute("role",claims.get("role"));
        // 验签通过后、放行前：黑名单检查（所有请求统一过闸）
        Long userId = Long.valueOf(claims.getSubject());
        if(rateLimitService.isBlocked(userId)){
            throw new BusinessException("账号已被临时限制，请明天再试");
        }
        return true;//校验通过，放行
    }
}
