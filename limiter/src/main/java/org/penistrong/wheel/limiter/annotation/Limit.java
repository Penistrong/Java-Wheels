package org.penistrong.wheel.limiter.annotation;

import java.lang.annotation.*;

/**
 * 限流注解, 未来可以扩展多种限流方式，这里没有使用Sentinel因为部分功能需要服务内部进行限流
 * 使用GuavaLimiter(本地)时，采用令牌桶算法
 * 使用RedisLimiter(分布式默认)时，采用滑动窗口限流算法
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Limit {

    /**
     * 限流资源的key, 保持唯一，不同的接口进行不同的流量控制
     */
    String key() default "";

    /**
     * 限流时间窗口大小，单位ms
     */
    long window() default 1000;

    /**
     * 最大流量
     */
    long limit() default 10;

    /**
     * 获取不到令牌时的最大等待时间，单位ms
     */
    long timeout() default 100;

    /**
     * 使用RedisLimiter时的有序集键过期时间, 单位s
     */
    long expire() default 10;

    /**
     * 请求降级时的提示语
     */
    String msg() default "接口限流，稍后再试";
}
