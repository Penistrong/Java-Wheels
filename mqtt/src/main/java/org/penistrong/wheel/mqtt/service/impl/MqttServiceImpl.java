package org.penistrong.wheel.mqtt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.penistrong.wheel.common.core.exception.CommonException;
import org.penistrong.wheel.common.core.exception.ErrorCode;
import org.penistrong.wheel.mqtt.config.msg.MqttMessageSender;
import org.penistrong.wheel.mqtt.service.MqttService;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.penistrong.wheel.mqtt.constant.MqttConstant.BIZ_PREFIX_OFFSET;

@Slf4j
@Service
@RequiredArgsConstructor
public class MqttServiceImpl implements MqttService {

    private final MqttPahoMessageDrivenChannelAdapter adapter;

    private final MqttMessageSender sender;

    @Override
    public void publish(String payload) {
        sender.publish(payload.getBytes());
    }

    @Override
    public void publish(String payload, String topic) {
        sender.publish(payload.getBytes(), topic);
    }

    @Override
    public void publishDelayed(String payload, String topic, Integer delayedSeconds) {
        // 根据延迟发布的API定义，在主题前添加前缀并指定延迟时间即可: $delayed/{delayInterval}/{topicName}
        sender.publish(payload.getBytes(), "$delayed/" + delayedSeconds + "/" + topic);
    }

    @Override
    public boolean subscribeTopic(String topic, Integer qos) {
        String[] topics = adapter.getTopic();
        if (!Arrays.asList(topics).contains(topic)) {
            try {
                adapter.addTopic(topic, qos);
                log.info("Subscribed topic: {}", topic);
                return true;
            } catch (MessagingException e) {
                log.error("Could not subscribe new topic: '{}'", topic);
                throw new CommonException(ErrorCode.FAIL_OPERATION);
            }
        }
        return false;
    }

    @Override
    public boolean unsubscribeTopic(String topic) {
        try {
            adapter.removeTopic(topic);
            log.info("Unsubscribed topic: {}", topic);
            return true;
        } catch (MessagingException e) {
            log.error("Could not unsubscribe topic: '{}'", topic);
            throw new CommonException(ErrorCode.FAIL_OPERATION);
        }
    }

    @Override
    public List<String> getSubscribedTopics() {
        return new ArrayList<>(Arrays.asList(adapter.getTopic()));
    }

    @Override
    public boolean changeTopicSuffix(String oldSuffix, String newSuffix) {
        // 将所有后缀为oldSuffix的主题取消订阅，并订阅新主题
        List<String> newSubscribeTopics = new ArrayList<>();
        String[] oldTopics = adapter.getTopic();
        for (String oldTopic: oldTopics) {
            newSubscribeTopics.add(oldTopic.substring(0, BIZ_PREFIX_OFFSET) + newSuffix);
        }
        try {
            // 修改当前缓存的订阅主题后缀
            // CSPServiceConstant.CURRENT_CONSUMER_TOPIC_SUFFIX = newSuffix;
            // 先新增订阅再批量取消旧主题的订阅
            adapter.addTopic(newSubscribeTopics.toArray(String[]::new));
            adapter.removeTopic(oldTopics);
            return true;
        } catch (MessagingException e) {
            log.error("Could not batch modify subscribed topics", e);
            throw new CommonException(ErrorCode.FAIL_OPERATION);
        }
    }
}
