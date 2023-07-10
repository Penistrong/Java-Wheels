package org.penistrong.wheel.limiter.aop;

import org.penistrong.wheel.limiter.constant.LimiterConfigConstant;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 自定义条件选择器，如果配置文件中没有开启限流则不开启AOP进行切面
 */
public class LimitAspectCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().containsProperty(LimiterConfigConstant.LIMIT_ENABLED) &&
                Boolean.TRUE.equals(context.getEnvironment().getProperty(LimiterConfigConstant.LIMIT_ENABLED, Boolean.class));
    }
}
