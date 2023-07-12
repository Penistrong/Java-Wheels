package org.penistrong.wheel.idempotence.config;

import jakarta.servlet.http.HttpServletRequest;
import org.penistrong.wheel.common.redis.service.RedisService;
import org.penistrong.wheel.idempotence.aop.NoRepeatSubmitAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after = {RedisAutoConfiguration.class})
@ConditionalOnProperty(prefix = "idempotence", name = "enabled", havingValue = "true", matchIfMissing = false)
public class IdempotenceAutoConfiguration {

    @Bean
    public NoRepeatSubmitAspect noRepeatSubmitAspect(RedisService redisService, HttpServletRequest request){
        return new NoRepeatSubmitAspect(redisService, request);
    }
}
