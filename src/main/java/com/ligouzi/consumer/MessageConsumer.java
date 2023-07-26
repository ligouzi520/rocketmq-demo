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
 * @author yezehao
 * @date 2023-07-24
 */
@Component
public class MessageConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);

    public MessageConsumer(RocketmqConfig rocketmqConfig) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(rocketmqConfig.getConsumerGroup());
        consumer.setNamesrvAddr(rocketmqConfig.getNameServer());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.subscribe(rocketmqConfig.getCustomTopic(), "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            try {
                MessageExt msg = msgs.get(0);
                LOGGER.info("msg key: [{}], msg body: [{}]", msg.getKeys(), new String(msg.getBody()));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            } catch (Exception e) {
                LOGGER.error("consume error, cause by: ", e);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });
        consumer.start();
        LOGGER.info("message consumer start ...");
    }

}
