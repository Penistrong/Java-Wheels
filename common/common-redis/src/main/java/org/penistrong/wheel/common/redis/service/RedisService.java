package org.penistrong.wheel.common.redis.service;

import io.lettuce.core.RedisException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 定义Redis常见操作用于调用
 */
@Slf4j
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void del(String... key) {
        if (Objects.nonNull(key) && key.length > 0) {
            redisTemplate.delete(Arrays.asList(key));
        }
    }

    /**
     * 获取Redis分布式锁
     * @param key   锁的键
     * @param value 锁的值
     * @param time  过期时间, 单位s
     * @return true or false
     */
    public boolean getLock(String key, Object value, long time) {
        final Object exist = redisTemplate.opsForValue().get(key);
        if (!ObjectUtils.isEmpty(exist)) {
            return false;
        }
        return Optional.ofNullable(redisTemplate.opsForValue().setIfAbsent(key, value, time, TimeUnit.SECONDS))
                .orElseGet(() -> {
                    log.error("Get redis distributed lock failed for key: [{}]", key);
                    return false;
                });
    }

    /**
     * 移除Redis分布式锁
     * @param key 锁的键
     */
    public void removeLock(String key) {
        try {
            redisTemplate.delete(key);
        } catch (RedisException e) {
            log.error("Remove redis distributed lock failed for key: [{}]", key);
        }
    }
}
