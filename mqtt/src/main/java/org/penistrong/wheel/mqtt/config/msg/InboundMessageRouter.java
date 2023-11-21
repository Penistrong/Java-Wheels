package org.penistrong.wheel.mqtt.config.msg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.penistrong.wheel.mqtt.enumeration.TopicResponseEnum;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.router.AbstractMessageRouter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import static org.penistrong.wheel.mqtt.constant.MqttConstant.CHANNEL_INBOUND;

/**
 * 入站消息路由, 当指定的入站管道出现新消息时将消息路由到其他入站管道中，比如可以分离处理不同主题或者payload的信息
 * 使用缓存机制避免每次路由都要去Spring上下文中查找对应的Bean
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InboundMessageRouter extends AbstractMessageRouter {

    private final ApplicationContext applicationContext;

    private static final ConcurrentHashMap<String, MessageChannel> channelsCache = new ConcurrentHashMap<>(16);

    @Override
    @Router(inputChannel = CHANNEL_INBOUND)
    protected Collection<MessageChannel> determineTargetChannels(Message<?> message) {
        MessageHeaders headers = message.getHeaders();
        String topic = headers.get(MqttHeaders.RECEIVED_TOPIC).toString();
        String payload = (String) message.getPayload();
        log.info("InboundMessageRouter received a message, from topic: '{}' with payload: '{}", topic, payload);
        TopicResponseEnum topicEnum = TopicResponseEnum.match(topic);
        if (channelsCache.containsKey(topicEnum.getChannelName())) {
            return Collections.singleton(channelsCache.get(topicEnum.getChannelName()));
        }
        MessageChannel channel = (MessageChannel) applicationContext.getBean(topicEnum.getChannelName());
        channelsCache.put(topicEnum.getChannelName(), channel);
        return Collections.singleton(channel);
    }
}
