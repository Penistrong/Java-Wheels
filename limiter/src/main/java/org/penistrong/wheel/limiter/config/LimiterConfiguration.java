package org.penistrong.wheel.limiter.config;

import org.penistrong.wheel.limiter.constant.LimiterConfigConstant;
import org.penistrong.wheel.limiter.manager.GuavaLimiter;
import org.penistrong.wheel.limiter.manager.LimiterManager;
import org.penistrong.wheel.limiter.manager.RedisLimiter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * 根据配置文件中的属性注入对应的配置类，以后可添加更多的LimiterManager实现类
 */
@Configuration
public class LimiterConfiguration {

    @Bean
    @ConditionalOnProperty(name = LimiterConfigConstant.LIMIT_TYPE, havingValue = "local")
    public LimiterManager guavaLimiter() {
        return new GuavaLimiter();
    }

    @Bean
    @ConditionalOnProperty(name = LimiterConfigConstant.LIMIT_TYPE, havingValue = "redis")
    public LimiterManager redisLimiter(StringRedisTemplate redisTemplate) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("redisLimiter.lua")));
        redisScript.setResultType(Long.class);
        return new RedisLimiter(redisTemplate, redisScript);
    }
}
