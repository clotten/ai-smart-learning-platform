package com.ai.learning.annotation;


import java.lang.annotation.*;

/**
 * 限流注解：标注在需要限流的方法上
 * 用法：\@RateLimit(business = "answer")
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    /**
     * 业务名（对应 application.yml 的 app.rate-limit.xxx）
     */
    String business();
}
