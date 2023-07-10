package org.penistrong.wheel.limiter.manager;

public interface LimiterManager {
    /**
     * 被限流注解标记的资源尝试获取限流令牌
     * @param limiter 使用的Limiter注解映射的实体类
     * @return true: 获取到令牌，false: 未获取到令牌
     */
    boolean tryAccess(Limiter limiter);
}
