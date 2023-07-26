/*
 * Copyright (c) 2023年 by XuanWu Wireless Technology Co.Ltd.
 *             All rights reserved
 */
package com.ligouzi.producer;

import com.ligouzi.config.RocketmqConfig;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author yezehao
 * @date 2023-07-24
 */
@Component
public class CustomMessageProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomMessageProducer.class);

    private final DefaultMQProducer producer;

    public CustomMessageProducer(RocketmqConfig rocketmqConfig) {
        producer = new DefaultMQProducer(rocketmqConfig.getCustomProducerGroup());
        producer.setRetryTimesWhenSendFailed(rocketmqConfig.getRetryTimes());
        producer.setNamesrvAddr(rocketmqConfig.getNameServer());
        start();
    }

    public DefaultMQProducer getProducer() {
        return this.producer;
    }

    public void start() {
        try {
            this.producer.start();
            LOGGER.info("custom message producer start ...");
        } catch (MQClientException e) {
            LOGGER.warn("custom message producer start error, cause by: ", e);
        }
    }

    public SendResult syncSend(String topic, String tag, String key, String body) throws Exception {
        try {
            Message message = new Message(topic, tag, key, body.getBytes());
            return this.producer.send(message);
        } catch (Exception e) {
            LOGGER.error("sync send message to rocketmq error, cause by: ", e);
            throw e;
        }
    }

    public void asyncSend(String topic, String tag, String key, String body) throws Exception {
        Message message = new Message(topic, tag, key, body.getBytes());
        try {
            this.producer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    LOGGER.info("async send message success, result: [{}]", sendResult.toString());
                }

                @Override
                public void onException(Throwable e) {
                    LOGGER.error("async send message to rocketmq error, cause by: ", e);
                }
            });
        } catch (Exception e) {
            LOGGER.error("async send message to rocketmq error, cause by: ", e);
            throw e;
        }
    }

    public void sendOneway(String topic, String tag, String key, String body) throws Exception {
        Message message = new Message(topic, tag, key, body.getBytes());
        try {
            this.producer.sendOneway(message);
        } catch (Exception e) {
            LOGGER.error("send oneway message to rocketmq error, cause by: ", e);
            throw e;
        }
    }

    public void shutdown() {
        this.producer.shutdown();
    }

}
