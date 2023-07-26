/*
 * Copyright (c) 2023年 by XuanWu Wireless Technology Co.Ltd.
 *             All rights reserved
 */
package com.ligouzi.producer;

import com.ligouzi.config.RocketmqConfig;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 顺序消息生产者
 * @author yezehao
 * @date 2023-07-26
 */
@Component
public class OrderMessageProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderMessageProducer.class);

    private final DefaultMQProducer producer;

    public OrderMessageProducer(RocketmqConfig rocketmqConfig) {
        this.producer = new DefaultMQProducer(rocketmqConfig.getOrderProducerGroup());
        this.producer.setRetryTimesWhenSendFailed(rocketmqConfig.getRetryTimes());
        this.producer.setNamesrvAddr(rocketmqConfig.getNameServer());
        start();
    }

    /**
     * 顺序发送
     * 调用了 SendResult send(Message msg, MessageQueueSelector selector, Object arg)方法。
     * MessageQueueSelector 是队列选择器，arg 是一个 Java Object 对象，可以传入作为消息发送分区的分类标准。
     * MessageQueueSelector 的接口中，mqs 是可以发送的队列，msg 是消息，arg 是上述 send 接口中传入的 Object 对象，返回的是该消息需要发送到的队列。
     * 生产环境中建议选择最细粒度的分区键进行拆分，例如，将订单ID、用户ID作为分区键关键字，可实现同一终端用户的消息按照顺序处理，不同用户的消息无需保证顺序。
     */
    public SendResult orderSend(String topic, String tag, String key, String body, Integer selectKey) throws Exception {
        Message message = new Message(topic, tag, key, body.getBytes());
        try {
            return this.producer.send(message, (MessageQueueSelector) (mqs, msg, arg) -> {
                Integer index = (Integer) arg;
                return mqs.get(index % mqs.size());
            }, selectKey);
        } catch (Exception e) {
            LOGGER.error("order send message to rocketmq error, cause by: ", e);
            throw e;
        }
    }

    public void start() {
        try {
            this.producer.start();
            LOGGER.info("custom message producer start ...");
        } catch (MQClientException e) {
            LOGGER.warn("custom message producer start error, cause by: ", e);
        }
    }

    public void shutdown() {
        this.producer.shutdown();
    }

}
