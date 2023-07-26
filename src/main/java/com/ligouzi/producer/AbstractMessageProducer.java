/*
 * Copyright (c) 2023年 by XuanWu Wireless Technology Co.Ltd.
 *             All rights reserved
 */
package com.ligouzi.producer;

import com.ligouzi.config.RocketmqConfig;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yezehao
 * @date 2023-07-26
 */
public abstract class AbstractMessageProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMessageProducer.class);

    protected DefaultMQProducer producer;

    protected String producerType;

    public AbstractMessageProducer(String producerGroup, RocketmqConfig rocketmqConfig, String producerType) {
        this.producer = new DefaultMQProducer(producerGroup);
        this.producer.setRetryTimesWhenSendFailed(rocketmqConfig.getRetryTimes());
        this.producer.setNamesrvAddr(rocketmqConfig.getNameServer());
        this.producerType = producerType;
        start();
    }

    public void start() {
        try {
            this.producer.start();
            LOGGER.info("{} message producer start ...", producerType);
        } catch (MQClientException e) {
            LOGGER.warn("{} message producer start error, cause by: ", producerType, e);
        }
    }

}
