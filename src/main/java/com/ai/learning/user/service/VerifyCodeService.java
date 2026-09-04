package com.ai.learning.user.service;

import com.ai.learning.common.BusinessException;
import com.ai.learning.common.service.RateLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 验证码服务：生成 -> 存Redis（5分钟） -> 发邮件 -> 校验（用后即焚）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VerifyCodeService {

    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;
    private final RateLimitService rateLimitService;

    private static final String CODE_KEY = "learn:code:email:";

    /**
     * 发送验证码（限流：同一邮箱 60 秒 1 次）
     */
    public void send(String email){
        //限流防刷
        if(!rateLimitService.tryLimit("code", email, 1, Duration.ofSeconds(60))){
            throw new BusinessException("发送太频繁，请60秒后再试");
        }
        //生成6位随机验证码
        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
        //存Redis：5分钟有效
        redisTemplate.opsForValue().set(CODE_KEY + email, code, Duration.ofMinutes(5));
        //发邮件
        emailService.sendCode(email, code);
        log.info("验证码已发送：{} -> {}", email, code);
    }

    /**
     * 校验验证码（用后即焚，防止重放）
     */
    public void verify(String email, String code){
        String key = CODE_KEY + email;
        String saved = redisTemplate.opsForValue().get(key);
        if(saved == null || !saved.equals(code)){
            throw new BusinessException("验证码错误或已过期");
        }
        redisTemplate.delete(key);
    }
}
