package org.penistrong.wheel.mqtt.service;

import java.util.List;

public interface MqttService {

    /**
     * 发布消息
     * @param payload 消息体
     */
    void publish(String payload);

    /**
     * 发布指定主题的消息
     * @param payload 消息体
     * @param topic 主题名
     */
    void publish(String payload, String topic);

    /**
     * 发布延迟消息，通过主题名增加前缀实现 $delayed/{DelayInterval}/{TopicName}
     * @param payload 消息体
     * @param topic 主题名
     * @param delayedSeconds 延迟秒数，最多4294967秒
     */
    void publishDelayed(String payload, String topic, Integer delayedSeconds);

    /**
     * 新增主题订阅
     * @param topic 主题名
     * @param qos 消息服务质量，只有0,1,2这三种等级
     * @return 是否订阅成功
     */
    boolean subscribeTopic(String topic, Integer qos);

    /**
     * 删除主题订阅
     * @param topic 主题名
     * @return 是否取消订阅成功
     */
    boolean unsubscribeTopic(String topic);

    /**
     * 获取当前订阅的所有主题名
     * @return 主题名列表
     */
    List<String> getSubscribedTopics();

    /**
     * 批量修改已订阅主题后缀
     * @param oldSuffix 旧后缀
     * @param newSuffix 新后缀
     * @return 是否修改成功
     */
    boolean changeTopicSuffix(String oldSuffix, String newSuffix);
}
