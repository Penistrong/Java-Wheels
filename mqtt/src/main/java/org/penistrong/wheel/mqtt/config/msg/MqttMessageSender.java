package org.penistrong.wheel.mqtt.config.msg;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import static org.penistrong.wheel.mqtt.constant.MqttConstant.CHANNEL_OUTBOUND;

@Service
@MessagingGateway(defaultRequestChannel = CHANNEL_OUTBOUND)
public interface MqttMessageSender {

    void publish(byte[] payload);

    void publish(byte[] payload, @Header(MqttHeaders.TOPIC) String topic);

    void publish(byte[] payload, @Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) Integer qos);
}
