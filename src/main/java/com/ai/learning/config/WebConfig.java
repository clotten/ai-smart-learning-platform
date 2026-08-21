package com.ai.learning.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置：注册拦截器 + 解决跨域
 */
@Configuration
public class WebConfig implements WebMvcConfigurer{

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")     //拦截所有 /api 接口
                .excludePathPatterns("/api/auth/login",
                        "/api/auth/register",   //登录注册放行
                        "/swagger-ui/**",       //Swagger 页面
                        "/v3/api-docs/**",      //接口数据
                        "/swagger-resources/**",
                        "/webjars/**");         //前端资源
    }

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
