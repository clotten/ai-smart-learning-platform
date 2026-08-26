package com.ai.learning.service;

import com.ai.learning.config.RateLimitProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 接口防护服务：防重 + 限流 + 黑名单 （三层防护）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;
    private final RateLimitProperties properties;

    //key 前缀集中管理（规范）
    private static final String DEDUP_KEY = "learn:dedup:answer:";
    private static final String LIMIT_KEY = "learn:limit:use:";
    private static final String VIOLATION_KEY ="learn:violation:user:";
    private static final String BLACKLIST_KEY ="learn:blacklist:user:";

    /**
     * 按业务名取配置
     */
    private RateLimitProperties.RateConfig getRateCongig(String business){
        if("ai".equals(business)) return properties.getAi();
        if("answer".equals(business)) return properties.getAnswer();
        return new RateLimitProperties.RateConfig(30, 60); //默认
    }

    /**
     * 防重：同一用户同一题 ttl 内只算一次
     */
    public boolean tryDedup(Long userId, Long questionId, Duration ttl){
        try{
            Boolean first= redisTemplate.opsForValue()
                    .setIfAbsent(DEDUP_KEY + userId + ":" +questionId, "1", ttl);
            return Boolean.TRUE.equals(first);
        } catch (Exception e) {
            //Redis异常降级：暂时放过，打印日志，不阻断正常业务
            log.error("防重Redis异常,降级放行",e);
            return true;
        }
    }

    /**
     * 限流：参数从配置读（按业务名）
     */
    public boolean tryLimit(String business, Long userId){
        RateLimitProperties.RateConfig cfg = getRateCongig(business);
        return tryLimit(business, userId, cfg.getMax(), Duration.ofSeconds(cfg.getWindowSeconds()));
    }
    /**
     * 限流：固定窗口计数，window内最多max次,手动指定参数（特殊场景用）
     */
    public boolean tryLimit(String business,Long userId, int max, Duration window){
        try{
            // key = learn:limit:user:answer:1  /  learn:limit:user:ai:1  ← 互不干扰！
            String key = LIMIT_KEY + business + ":" + userId;
            Long count = redisTemplate.opsForValue().increment(key);
            if(count != null && count ==1){
                redisTemplate.expire(key, window); //第一个请求开始计时
            }
            if(count != null && count > max){
                addViolation(userId);   //触发限流 -> 记一次违规
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("限流Redis异常，降级放行",e);
            return true;
        }
    }


    /**
     * 违规累计 + 拉黑（参数从配置读）
     */
    private void addViolation(Long userId){
        String key = VIOLATION_KEY + userId;
        Long v = redisTemplate.opsForValue().increment(key);
        if(v != null && v == 1){
            redisTemplate.expire(key, Duration.ofSeconds(properties.getBlacklist().getWindowsSeconds()));
        }
        if(v != null && v >= properties.getBlacklist().getViolations()){
            redisTemplate.opsForValue().set(
                    BLACKLIST_KEY + userId, "1",
                    Duration.ofDays(properties.getBlacklist().getBanDays()));
            log.warn("用户 {} 一小时超限 {} 次，拉黑 {} 天", userId,
                    properties.getBlacklist().getViolations(),
                    properties.getBlacklist().getBanDays());
        }
    }

    /**
     * 3.黑名单检查
     */
    public boolean isBlocked(Long userId){
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_KEY + userId));
    }



    /**
     * 限流：String标识（邮箱/手机号等）
     */
    public boolean tryLimit(String business,String identifier, int max, Duration window){
        try{
            String key = LIMIT_KEY + business + ":" + identifier;
            Long count = redisTemplate.opsForValue().increment(key);
            if(count != null && count ==1){
                redisTemplate.expire(key, window); //第一个请求开始计时
            }
            if(count != null && count > max){
                addViolation(identifier.hashCode() + 0L);   //触发限流 -> 记一次违规(String版本简单处理）
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("限流Redis异常，降级放行",e);
            return true;
        }
    }


}
