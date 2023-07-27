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
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 批量消息生产者
 * @author yezehao
 * @date 2023-07-27
 */
@Component
public class BatchMessageProducer extends AbstractMessageProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchMessageProducer.class);

    private static final String PRODUCER_TYPE = "batch";

    public BatchMessageProducer(RocketmqConfig rocketmqConfig) {
        super(rocketmqConfig.getBatchProducerGroup(), rocketmqConfig, PRODUCER_TYPE);
    }

    /**
     * 批量发送
     * 需要注意的是批量消息的大小不能超过 1MiB（否则需要自行分割），其次同一批 batch 中 topic 必须相同。
     */
    public SendResult batchSend(List<Message> messages) throws Exception {
        if (CollectionUtils.isEmpty(messages)) {
            throw new Exception();
        }
        try {
            return producer.send(messages);
        } catch (Exception e) {
            LOGGER.error("batch send message to rocketmq error, cause by: ", e);
            throw e;
        }
    }

}
