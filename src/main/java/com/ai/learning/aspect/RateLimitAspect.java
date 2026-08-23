package com.ai.learning.aspect;


import com.ai.learning.annotation.RateLimit;
import com.ai.learning.common.BusinessException;
import com.ai.learning.service.RateLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 限流切面：拦截所有 \@RateLimit 注解的方法，统一做 Redis 限流
 * 业务方法里看不到任何限流代码
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RateLimitService rateLimitService;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable{
        Long userId = getCurrentUserId();

        //限流检查（参数从配置读，RateLimitService内部处理）
        if(!rateLimitService.tryLimit(rateLimit.business(),userId)){
            throw new BusinessException("操作太频繁，请稍后再试");
        }
        //放行：继续执行业务方法
        return joinPoint.proceed();
    }

    /**
     * 从当前请求（拦截器存过）那用户id
     */
    private Long getCurrentUserId(){
        ServletRequestAttributes attrs
                =(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attrs == null) return null;
        Object userId = attrs.getRequest().getAttribute("userId");
        return userId == null ? null : Long.valueOf(userId.toString());
    }
}
