package com.ai.learning.service;

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

    //key 前缀集中管理（规范）
    private static final String DEDUP_KEY = "learn:dedup:answer:";
    private static final String LIMIT_KEY = "learn:limit:use:";
    private static final String VIOLATION_KEY ="learn:violation:user:";
    private static final String BLACKLIST_KEY ="learn:blacklist:user:";

    /**
     * 1.f防重：同一用户同一题 ttl 内只算一次
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
     * 2.限流：固定窗口计数，window内最多max次
     */
    public boolean tryLimit(Long userId, int max, Duration window){
        try{
            String key = LIMIT_KEY + userId;
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
     * 违规累计：一小时内超限 3 次 -> 拉黑 1 天
     */
    private void addViolation(Long userId){
        String key = VIOLATION_KEY + userId;
        Long v = redisTemplate.opsForValue().increment(key);
        if(v != null && v == 1){
            redisTemplate.expire(key, Duration.ofHours(1));
        }
        if(v != null && v >= 3){
            redisTemplate.opsForValue().set(BLACKLIST_KEY + userId, "1", Duration.ofDays(1));
            log.warn("用户 {} 一小时超限3次，拉黑1天",userId);
        }
    }

    /**
     * 3.黑名单检查
     */
    public boolean isBlocked(Long userId){
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_KEY + userId));
    }
}
