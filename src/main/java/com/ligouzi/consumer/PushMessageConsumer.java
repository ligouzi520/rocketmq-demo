/*
 * Copyright (c) 2023年 by XuanWu Wireless Technology Co.Ltd.
 *             All rights reserved
 */
package com.ligouzi.consumer;

import com.ligouzi.config.RocketmqConfig;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Push消息消费者
 * @author yezehao
 * @date 2023-07-24
 */
@Component
public class PushMessageConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PushMessageConsumer.class);

    /**
     * 初始化消费者时，必须填写ConsumerGroupName，同一个消费组的ConsumerGroupName是相同的，这是判断消费者是否属于同一个消费组的重要属性。
     * 然后是设置NameServer地址。
     * 然后是调用subscribe方法订阅Topic，subscribe方法需要指定需要订阅的Topic名，也可以增加消息过滤的条件，比如TagA等，上述代码中指定*表示接收所有tag的消息。
     * 除了订阅之外，还需要注册回调接口编写消费逻辑来处理从Broker中收到的消息，调用registerMessageListener方法，需要传入MessageListener的实现。
     *
     */
    public PushMessageConsumer(RocketmqConfig rocketmqConfig) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(rocketmqConfig.getPushConsumerGroup());
        consumer.setNamesrvAddr(rocketmqConfig.getNameServer());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.subscribe(rocketmqConfig.getCustomTopic(), "*");
        /**
         * 代码中采用并发消费，因此是MessageListenerConcurrently的实现
         * @param msgs 从Broker端获取的需要被消费消息列表
         * @return 消费状态 ConsumeConcurrentlyStatus.CONSUME_SUCCESS：成功，ConsumeConcurrentlyStatus.RECONSUME_LATER：失败
         */
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            try {
                MessageExt msg = msgs.get(0);
                LOGGER.info("push message consumer receive msg, key: [{}], body: [{}]", msg.getKeys(), new String(msg.getBody()));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            } catch (Exception e) {
                LOGGER.error("push consume error, cause by: ", e);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });
        consumer.start();
        LOGGER.info("push message consumer start ...");
    }

}
