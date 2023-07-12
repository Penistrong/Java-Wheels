package org.penistrong.wheel.idempotence.aop;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.penistrong.wheel.common.core.mvc.CommonResult;
import org.penistrong.wheel.common.redis.service.RedisService;
import org.penistrong.wheel.idempotence.annotation.NoRepeatSubmit;
import org.penistrong.wheel.idempotence.exception.IdempotenceException;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.penistrong.wheel.idempotence.constant.IdempotenceConfigConstant.*;

/**
 * 接口幂等性插件-防止重复提交切面
 * <p>被{@link org.penistrong.wheel.idempotence.annotation.NoRepeatSubmit}注解修饰的方法，会在执行前后进行拦截
 * <p>使用ThreadLocal和Redis保存切入点方法前置通知生成的的唯一key，不论方法执行成功或失败，最终都会删除该key
 */
@Slf4j
@RequiredArgsConstructor
@Aspect
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class NoRepeatSubmitAspect {

    private static final ThreadLocal<String> KEY_CACHE = new ThreadLocal<>();

    private final RedisService redisService;

    // HttpServletRequest被注入时，IOC容器在依赖查找时寻找到的是RequestObjectFactory
    // 其内部使用了RequestContextHolder利用ThreadLocal获取当前线程的HttpServletRequest
    // 而这个RequestObjectFactory会被AutowireUtils创建一个代理对象
    // 最终由ObjectFactoryDelegatingInvocationHandler调用invoke方法触发当前线程对应的HttpServletRequest中的方法
    private final HttpServletRequest request;

    @Pointcut("@annotation(noRepeatSubmit)")
    private void checkRepeatSubmit(NoRepeatSubmit noRepeatSubmit) {}

    /**
     * 前置通知，用于拦截验证是否重复提交，使用请求url + 请求参数 + JWT token生成唯一Key
     * @param joinPoint 切入点
     * @param noRepeatSubmit 防止重复提交注解
     */
    @Before(value = "checkRepeatSubmit(noRepeatSubmit)", argNames = "joinPoint,noRepeatSubmit")
    public void doBefore(JoinPoint joinPoint, NoRepeatSubmit noRepeatSubmit) {
        long interval = noRepeatSubmit.interval() > 0 ? noRepeatSubmit.timeUnit().toMillis(noRepeatSubmit.interval()) : 0;

        String url = request.getRequestURI();

        String params = paramsArrayToString(joinPoint.getArgs());
        // 利用JWT(不存在JWT则使用IP地址替代)拼接参数后计算MD5值
        String mixedMD5 = Optional.ofNullable(request.getHeader(HEADER_AUTHORIZATION_KEY))
                .map(StringUtils::trimToEmpty)
                .orElseGet(() -> request.getHeader(HEADER_IP_KEY));
        mixedMD5 = SecureUtil.md5(mixedMD5 + ":" + params);

        String submitKey = IDEMPOTENCE_NO_REPEAT_SUBMIT_KEY_CACHE_PREFIX + ":" + url + ":" + mixedMD5;
        if (redisService.getLock(submitKey, "", TimeUnit.MILLISECONDS.toSeconds(interval))) {
            KEY_CACHE.set(submitKey);
        } else {
            log.warn("[Repeat Submit Detected] Url: '{}' with params: {}", url, params);
            throw new IdempotenceException(noRepeatSubmit.message());
        }
    }

    /**
     * 返回通知，用于清除缓存
     * @param joinPoint 切入点
     * @param noRepeatSubmit 防止重复提交注解
     * @param result 返回值, 统一响应结果类型为CommonResult
     */
    @AfterReturning(pointcut = "checkRepeatSubmit(noRepeatSubmit)", returning = "result", argNames = "joinPoint,noRepeatSubmit,result")
    public void doAfterReturning(JoinPoint joinPoint, NoRepeatSubmit noRepeatSubmit, Object result) {
        if (result instanceof CommonResult) {
            try {
                CommonResult<?> r = (CommonResult<?>) result;
                if (r.success())
                    return;
                redisService.removeLock(KEY_CACHE.get());
            } finally {
                KEY_CACHE.remove();
            }
        }
    }

    /**
     * 拦截异常，用于清除缓存
     * @param joinPoint 切入点
     * @param noRepeatSubmit 防止重复提交注解
     * @param e 异常信息
     */
    @AfterThrowing(pointcut = "checkRepeatSubmit(noRepeatSubmit)", throwing = "e", argNames = "joinPoint,noRepeatSubmit,e")
    public void doAfterThrowing(JoinPoint joinPoint, NoRepeatSubmit noRepeatSubmit, Exception e) {
        redisService.removeLock(KEY_CACHE.get());
        KEY_CACHE.remove();
    }

    private String paramsArrayToString(Object[] paramsArray) {
        StringBuilder params = new StringBuilder();
        Optional.ofNullable(paramsArray)
                .stream()
                .filter(param -> !paramFilter(param))
                .map(JSONUtil::toJsonStr)
                .forEach(param -> params.append(param).append(" "));
        return params.toString().trim();
    }

    /**
     * 过滤不需要处理的参数，主要是MultipartFile对象
     * @param o 参数对象
     * @return true: 不需要处理 false: 需要处理
     */
    public boolean paramFilter(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection<?> collection = (Collection<?>) o;
            if (!collection.isEmpty()) {
                return collection.iterator().next() instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map<?, ?> map = (Map<?, ?>) o;
            if (!map.isEmpty()) {
                return map.values().iterator().next() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile
                || o instanceof HttpServletRequest
                || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }
}
