package org.penistrong.wheel.mqtt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.penistrong.wheel.mqtt.constant.MqttConstant.THREADPOOL_INBOUND;
import static org.penistrong.wheel.mqtt.constant.MqttConstant.THREADPOOL_OUTBOUND;

/**
 * MQTT信道对应线程池配置类
 */
@Configuration
@ConfigurationProperties(prefix = "mqtt.channel.thread-pool")
public class MqttThreadPoolConfiguration {

    private int corePoolSize = 10;
    private int maximumPoolSize = 20;
    private long keepAliveTime = 10;
    private int taskQueueCapacity = 100;

    @Bean(name = THREADPOOL_INBOUND)
    public Executor inboundThreadPool() {
        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(taskQueueCapacity),
                new CustomizableThreadFactory(THREADPOOL_INBOUND + "-channel-"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Bean(name = THREADPOOL_OUTBOUND)
    public Executor outboundThreadPool() {
        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(taskQueueCapacity),
                new CustomizableThreadFactory(THREADPOOL_OUTBOUND + "-channel-"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

}
