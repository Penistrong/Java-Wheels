package org.penistrong.wheel.limiter.manager;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GuavaLimiter implements LimiterManager {

    private final Map<String, RateLimiter> limiterMap = Maps.newConcurrentMap();

    @Override
    public boolean tryAccess(Limiter limiter) {
        RateLimiter rateLimiter = getRateLimiter(limiter);

        if (Objects.isNull(rateLimiter)){
            return false;
        }
        boolean canAccess = rateLimiter.tryAcquire(limiter.getTimeout(), TimeUnit.MILLISECONDS);

        log.info("Resource [{}] try to acquire limiter-token, result is [{}]", limiter.getResourceKey(), canAccess);

        return canAccess;
    }

    public RateLimiter getRateLimiter(Limiter limiter) {
        String key = limiter.getResourceKey();
        if (limiterMap.containsKey(key)) {
            return limiterMap.get(key);
        }
        // PPS(Permits Per Second)设置为@Limit注解定义的qps值(粒度只为调用对应方法的频率)
        // qps = limit / (window / 1000)
        RateLimiter rateLimiter = RateLimiter.create(
                (double) limiter.getLimit() / TimeUnit.MILLISECONDS.toSeconds(limiter.getWindow())
        );
        limiterMap.put(key, rateLimiter);
        return rateLimiter;
    }
}
