package org.penistrong.wheel.limiter.manager;

import cn.hutool.core.util.RandomUtil;
import org.penistrong.wheel.limiter.exception.LimiterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class RedisLimiter implements LimiterManager{

    private final StringRedisTemplate redisTemplate;

    private final RedisScript<Long> redisScript;

    @Override
    public boolean tryAccess(Limiter limiter) {
        String resourceKey = Optional.ofNullable(limiter.getResourceKey()).orElseThrow(
                () -> new LimiterException("Resource key must not be null")
        );

        // result > 0说明获取到了分布式锁且返回值为当前滑动窗口内的请求数量，可以继续执行业务逻辑
        long curTime = System.currentTimeMillis();
        Long result = redisTemplate.execute(
                redisScript,
                Collections.singletonList(resourceKey),
                String.valueOf(curTime),
                String.valueOf(limiter.getWindow()),
                String.valueOf(limiter.getLimit()),
                String.valueOf(limiter.getTimeout()),
                String.valueOf(limiter.getExpire()),
                curTime + "-" + RandomUtil.randomInt()
        );

        log.info("Resource [{}] try to acquire limiter-token[{}/{}], result is [{}]",
                resourceKey,
                result > 0 ? result : -1,
                limiter.getLimit(),
                result > 0);

        return result > 0;
    }
}
