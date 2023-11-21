package org.penistrong.wheel.mqtt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageChannel;

import java.util.concurrent.Executor;

import static org.penistrong.wheel.mqtt.constant.MqttConstant.*;

/**
 * MQTT信道配置类
 */
@Configuration
public class MqttChannelConfiguration {

    // 入站、出站信道使用的线程池
    @Autowired
    @Qualifier(THREADPOOL_INBOUND)
    private Executor inboundThreadPool;

    @Autowired
    @Qualifier(THREADPOOL_OUTBOUND)
    private Executor outboundThreadPool;

    @Bean(name = CHANNEL_INBOUND)
    public MessageChannel mqttInboundChannel() {
        return new ExecutorChannel(inboundThreadPool);
    }

    @Bean(name = CHANNEL_OUTBOUND)
    public MessageChannel mqttOutboundChannel() {
        return new ExecutorChannel(outboundThreadPool);
    }

    @Bean(name = CHANNEL_BROADCAST)
    public MessageChannel mqttBroadcastChannel() {
        return new PublishSubscribeChannel();
    }

    @Bean(name = CHANNEL_ERROR)
    public MessageChannel mqttErrorChannel() {
        return new DirectChannel();
    }

    @Bean(name = CHANNEL_DEFAULT)
    public MessageChannel mqttDefaultChannel() {
        return new DirectChannel();
    }

}
