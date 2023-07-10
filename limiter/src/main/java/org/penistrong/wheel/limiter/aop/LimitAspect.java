package org.penistrong.wheel.limiter.aop;

import lombok.Setter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.penistrong.wheel.limiter.annotation.Limit;
import org.penistrong.wheel.limiter.exception.LimiterException;
import org.penistrong.wheel.limiter.manager.Limiter;
import org.penistrong.wheel.limiter.manager.LimiterManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@Aspect
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Component
@Conditional(LimitAspectCondition.class)
public class LimitAspect {

    @Setter(onMethod_ = @Autowired)
    private LimiterManager limiterManager;

    @Pointcut("@annotation(limit)")
    private void checkLimit(Limit limit) {}

    @Before(value = "checkLimit(limit)", argNames = "joinPoint,limit")
    public void before(JoinPoint joinPoint, Limit limit) {
        Limiter limiter = Limiter.builder()
                .resourceKey(limit.key())
                .window(limit.window())
                .limit(limit.limit())
                .timeout(limit.timeout())
                .expire(limit.expire())
                .msg(limit.msg())
                .build();

        if (!limiterManager.tryAccess(limiter)) {
            throw new LimiterException(limiter.getMsg());
        }
    }
}
