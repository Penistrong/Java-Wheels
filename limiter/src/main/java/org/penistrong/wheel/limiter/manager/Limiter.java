package org.penistrong.wheel.limiter.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 映射Limit注解中的属性值，在LimitAspect切面中构造Limiter
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Limiter {
    /**
     * 唯一资源Key
     */
    String resourceKey;

    /**
     * 限流时间窗口大小，单位ms
     */
    long window;

    /**
     * 最大流量
     */
    long limit;

    /**
     * 获取不到令牌时的最大等待时间, 超过该时间直接降级请求
     */
    long timeout;

    /**
     * 使用RedisLimiter时设置的Redis过期时间
     */
    long expire;

    /**
     * 请求降级时的提示语
     */
    String msg;
}
