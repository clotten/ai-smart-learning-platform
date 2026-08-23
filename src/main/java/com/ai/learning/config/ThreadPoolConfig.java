package com.ai.learning.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 全局线程池配置：所有异步/耗时任务共用
 */
@Configuration
public class ThreadPoolConfig {

    @Bean(name = "aiExecutor")
    public Executor aiExecutor(){
        ThreadPoolTaskExecutor  executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);        //核心线程数（平时保留）
        executor.setMaxPoolSize(8);         //高峰期最大线程数
        executor.setQueueCapacity(50);     //排队等待的任务数
        executor.setKeepAliveSeconds(60);   //空闲线程存活时间
        executor.setThreadNamePrefix("ai-pool-");   //线程名前缀（查日志用）
        //队列满+线程满时的策略：调用线程执行（牺牲性能不丢任务）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
