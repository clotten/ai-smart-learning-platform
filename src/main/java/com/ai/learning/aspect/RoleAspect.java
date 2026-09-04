package com.ai.learning.aspect;


import com.ai.learning.annotation.RequiresRole;
import com.ai.learning.common.BusinessException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class RoleAspect {
    @Around("@annotation(requiresRole)")
    public Object around(ProceedingJoinPoint pjp, RequiresRole requiresRole) throws Throwable{
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Object role = attrs.getRequest().getAttribute("role");
        if(requiresRole.value() == 1 && !"1".equals(String.valueOf(role))){
            throw new BusinessException("无权操作");
        }
        return pjp.proceed();
    }
}
