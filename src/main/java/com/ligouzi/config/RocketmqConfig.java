/*
 * Copyright (c) 2023年 by XuanWu Wireless Technology Co.Ltd.
 *             All rights reserved
 */
package com.ligouzi.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author yezehao
 * @date 2023-07-24
 */
@Data
@Component
public class RocketmqConfig {

    @Value("${rocketmq.nameServer}")
    private String nameServer;

    @Value("${rocketmq.retryTimes}")
    private Integer retryTimes;

    @Value("${rocketmq.topic}")
    private String customTopic;

    @Value("${rocketmq.producer.customGroup}")
    private String customProducerGroup;

    @Value("${rocketmq.producer.orderGroup}")
    private String orderProducerGroup;

    @Value("${rocketmq.producer.batchGroup}")
    private String batchProducerGroup;

    @Value("${rocketmq.producer.scheduledGroup}")
    private String scheduledProducerGroup;

    @Value("${rocketmq.producer.transactionGroup}")
    private String transactionProducerGroup;

    @Value("${rocketmq.consumer.pushGroup}")
    private String pushConsumerGroup;

    @Value("${rocketmq.consumer.subscribePullGroup}")
    private String subscribePullConsumerGroup;

    @Value("${rocketmq.consumer.assignPullGroup}")
    private String assignPullConsumerGroup;

}
