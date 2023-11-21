package org.penistrong.wheel.mqtt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageHandler;

import static org.penistrong.wheel.mqtt.constant.MqttConstant.CHANNEL_OUTBOUND;

/**
 * MQTT生产者配置类
 */
@Configuration
public class MqttProducerConfiguration {

    @Autowired
    private MqttConnectionConfiguration mqttConnConfig;

    @Bean
    @ServiceActivator(inputChannel = CHANNEL_OUTBOUND)
    public MessageHandler getMqttProducer(MqttPahoClientFactory factory) {
        MqttPahoMessageHandler handler = new MqttPahoMessageHandler(mqttConnConfig.getOutClientId(), factory);
        DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter();
        converter.setPayloadAsBytes(true);
        handler.setAsync(true);
        handler.setConverter(converter);
        handler.setDefaultQos(mqttConnConfig.getProducerQos());
        handler.setDefaultTopic(mqttConnConfig.getDefaultTopic());
        return handler;
    }
}
