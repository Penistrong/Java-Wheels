package org.penistrong.wheel.mqtt.config.msg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Service;

import static org.penistrong.wheel.mqtt.constant.MqttConstant.CHANNEL_DEFAULT;

@Slf4j
@Service
@RequiredArgsConstructor
public class MqttMessageReceiver {

//    private final GatewayAlarmService gatewayAlarmService;
//
//    @Bean
//    @ServiceActivator(inputChannel = CHANNEL_INBOUND)
//    public MessageHandler handleGatewayResponse() {
//        return msg -> {
//            String topic = (String) msg.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
//            String payload = new String((byte[]) msg.getPayload());
//            log.info("Received Gateway Response, from topic: '{}', with payload: '{}'", topic, payload);
//            gatewayAlarmService.processGatewayAlarmResponseMessage(topic.substring(0, GATEWAY_ID_OFFSET), payload);
//        };
//    }

    @Bean
    @ServiceActivator(inputChannel = CHANNEL_DEFAULT)
    public MessageHandler handleUnknownDestinationMsg() {
        return msg -> log.info("Received unregistered topic's message, from topic: '{}', with payload: '{}'",
                                msg.getHeaders().get(MqttHeaders.RECEIVED_TOPIC),
                                new String((byte[]) msg.getPayload()));
    }
}
