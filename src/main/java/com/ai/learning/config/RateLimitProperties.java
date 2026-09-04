package com.ai.learning.config;


import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 限流配置：自动绑定 application.yml 的 app.rate-limit.*
 * 改配置不用改代码，重启即生效
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {

    @PostConstruct
    public void print() {
        System.out.println("AI限流: " + ai.getMax() + "次/" + ai.getWindowSeconds() + "秒");
        System.out.println("答题限流: " + answer.getMax() + "次/" + answer.getWindowSeconds() + "秒");
        System.out.println("拉黑规则: " + blacklist.getViolations() + "次/" + blacklist.getBanDays() + "天");
    }
    /**
     * 限流参数
     */
    @Data
    public static class RateConfig{
        private int max = 30;
        private int windowSeconds = 60;

        public RateConfig(){ }
        public RateConfig(int max, int windowSeconds){
            this.max = max;
            this.windowSeconds = windowSeconds;
        }
    }

    /**
     * 黑名单参数
     */
    @Data
    public static class BlacklistConfig{
        private int violations = 3;
        private int windowSeconds = 3600;
        private int banDays = 1;
    }
    /**
     * AI对话限流
     */
    private RateConfig ai = new RateConfig(5, 60);

    /**
     * 答题限流
     */
    private RateConfig answer = new RateConfig(30, 60);

    /**
     * 黑名单规则
     */
    private BlacklistConfig blacklist = new BlacklistConfig();


}
