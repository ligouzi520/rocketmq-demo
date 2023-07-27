/*
 * Copyright (c) 2023年 by XuanWu Wireless Technology Co.Ltd.
 *             All rights reserved
 */
package com.ligouzi.producer;

import com.ligouzi.config.RocketmqConfig;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 延时消息生产者
 * @author yezehao
 * @date 2023-07-26
 */
@Component
public class ScheduledMessageProducer extends AbstractMessageProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledMessageProducer.class);

    private static final String PRODUCER_TYPE = "scheduled";

    public ScheduledMessageProducer(RocketmqConfig rocketmqConfig) {
        super(rocketmqConfig.getScheduledProducerGroup(), rocketmqConfig, PRODUCER_TYPE);
    }

    /**
     * 延时发送
     * 延时等级对应的延迟时间：
     * Apache RocketMQ 一共支持18个等级的延迟投递
     * 1-1s，2-5s，3-10s，4-30s，5-1min，6-2min，7-3min，8-4min，9-5min，10-6min，11-7min，12-8min，13-9min，14-10min，15-20min，16-30min，17-1h，18-2h
     * @param delayTimeLevel 延时等级
     */
    public SendResult scheduledSend(Message message, Integer delayTimeLevel) throws Exception {
        if (delayTimeLevel == null) {
            throw new Exception();
        }
        try {
            message.setDelayTimeLevel(delayTimeLevel);
            return producer.send(message);
        } catch (Exception e) {
            LOGGER.error("scheduled send message to rocketmq error, cause by: ", e);
            throw e;
        }
    }

}
