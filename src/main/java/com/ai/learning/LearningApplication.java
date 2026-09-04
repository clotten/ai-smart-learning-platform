package com.ai.learning;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI 智能学习平台 - 启动类
 */
@SpringBootApplication
@MapperScan("com.ai.learning.**.mapper")

public class LearningApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningApplication.class, args);
        System.out.println("🚀 AI 智能学习平台启动成功: http://localhost:8081");
    }
}
