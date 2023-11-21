package org.penistrong.wheel.mqtt.enumeration;

import lombok.Getter;

import java.util.Arrays;
import java.util.regex.Pattern;

import static org.penistrong.wheel.mqtt.constant.MqttConstant.*;


/**
 * 入站消息路由需要使用的响应消息主题枚举类
 * 为了未来扩展需要，新增不同业务的响应消息时只需要在当前枚举类中定义新的响应消息主题枚举即可
 */
@Getter
public enum TopicResponseEnum {

    BIZ_RESPONSE(Pattern.compile("^" + BIZ_PREFIX + LETTER_REGEX + "$"), CHANNEL_INBOUND),
    DEFAULT(Pattern.compile("^.*$"), CHANNEL_DEFAULT);

    private final Pattern pattern;
    private final String channelName;

    TopicResponseEnum(Pattern pattern, String channelName) {
        this.pattern = pattern;
        this.channelName = channelName;
    }

    public static TopicResponseEnum match(String topic) {
        return Arrays.stream(TopicResponseEnum.values())
                .filter(topicResponseEnum -> topicResponseEnum.pattern
                        .matcher(topic).matches())
                .findFirst()
                .orElse(DEFAULT);
    }
}
