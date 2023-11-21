package org.penistrong.wheel.mqtt.config;


import lombok.extern.slf4j.Slf4j;
import org.penistrong.wheel.mqtt.dto.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import static org.penistrong.wheel.mqtt.constant.MqttConstant.*;

/**
 * MQTT消费者配置类
 */
@Slf4j
@Configuration
public class MqttConsumerConfiguration {

    @Autowired
    private MqttConnectionConfiguration mqttConnConfig;

    @Autowired
    @Qualifier(CHANNEL_INBOUND)
    private MessageChannel inboundChannel;

    @Autowired
    @Qualifier(CHANNEL_ERROR)
    private MessageChannel errorChannel;

    // 作为消费者时，如果订阅主题发生变更，获取当前管道适配器Bean adapter并修改订阅的topic即可
    // 如果配置文件中包含初始订阅的主题名，可以该Bean创建过程中添加订阅
    @Bean
    public MqttPahoMessageDrivenChannelAdapter adapter(MqttPahoClientFactory factory) {
        String[] topicArray = mqttConnConfig.subscribeTopics.stream().map(Topic::getTopicName).toArray(String[]::new);
        return new MqttPahoMessageDrivenChannelAdapter(mqttConnConfig.getInClientId(), factory, topicArray);
    }

    @Bean
    public MessageProducer getMqttConsumer(MqttPahoMessageDrivenChannelAdapter adapter) {
        DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter();
        converter.setPayloadAsBytes(true);
        adapter.setConverter(converter);
        adapter.setOutputChannel(inboundChannel);
        adapter.setErrorChannel(errorChannel);
        adapter.setQos(mqttConnConfig.getConsumerQos());
        adapter.setCompletionTimeout(mqttConnConfig.getCompletionTimeout());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = CHANNEL_DEFAULT)
    public MessageHandler defaultInboundHandler() {
        return msg -> log.info("Default channel received message but can not process, from topic: '{}' with payload: '{}'",
                                msg.getHeaders().get(MqttHeaders.RECEIVED_TOPIC),
                                msg.getPayload());
    }
}
