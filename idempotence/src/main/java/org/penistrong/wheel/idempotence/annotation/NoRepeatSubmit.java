package org.penistrong.wheel.idempotence.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoRepeatSubmit {

    int interval() default 1000;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    String message() default "请勿重复提交";
}
