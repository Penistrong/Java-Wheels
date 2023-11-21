package org.penistrong.wheel.mqtt.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.penistrong.wheel.mqtt.dto.Topic;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

import java.util.List;

/**
 * MQTT连接配置类
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "mqtt")
public class MqttConnectionConfiguration {

    String[] brokerUrls;
    String username;
    String password;
    String inClientId;
    String outClientId;
    int keepAlive;                                      // 连接保活时间，单位second
    boolean cleanSession = false;                       // 断连时是否保留会话，Broker根据同一clientId恢复会话，不会丢失断连期间的消息
    String defaultTopic = "unknown-destination-topic";  // 无法解析目标投递主题时使用的默认主题名
    int producerQos = 2;
    int consumerQos = 2;
    long completionTimeout = 5000L;                     // 消息消费时间上限，单位millisecond
    List<Topic> subscribeTopics;

    public MqttConnectOptions getMqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(brokerUrls);
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setKeepAliveInterval(keepAlive);
        // 设置自动重连，防止MQTT Broker短暂宕机或者启动时Broker尚未启动的问题
        options.setAutomaticReconnect(true);
        return options;
    }

    @Bean
    public MqttPahoClientFactory mqttPahoClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(getMqttConnectOptions());
        return factory;
    }
}
