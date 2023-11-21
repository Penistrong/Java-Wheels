package org.penistrong.wheel.mqtt.dto;

import lombok.Data;

/**
 * MQTT订阅的Topic名称及其对应的qos
 */
@Data
public class Topic {

    private String topicName;

    private int qos;
}
