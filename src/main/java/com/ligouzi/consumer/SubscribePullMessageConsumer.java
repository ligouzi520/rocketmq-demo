/*
 * Copyright (c) 2023年 by XuanWu Wireless Technology Co.Ltd.
 *             All rights reserved
 */
package com.ligouzi.consumer;

import com.ligouzi.config.RocketmqConfig;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Pull消息消费者
 * @author yezehao
 * @date 2023-07-27
 */
@Component
public class SubscribePullMessageConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscribePullMessageConsumer.class);

    private final DefaultLitePullConsumer subscribePullConsumer;

    /**
     * LitePullConsumer拉取消息调用的是轮询poll接口，如果能拉取到消息则返回对应的消息列表，否则返回null。
     * LitePullConsumer默认是自动提交位点。
     * 在subscribe模式下，同一个消费组下的多个LitePullConsumer会负载均衡消费，与PushConsumer一致。
     */
    public SubscribePullMessageConsumer(RocketmqConfig rocketmqConfig) throws MQClientException {
        subscribePullConsumer = new DefaultLitePullConsumer(rocketmqConfig.getSubscribePullConsumerGroup());
        subscribePullConsumer.setNamesrvAddr(rocketmqConfig.getNameServer());
        subscribePullConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        subscribePullConsumer.setMessageModel(MessageModel.CLUSTERING);
        subscribePullConsumer.subscribe(rocketmqConfig.getCustomTopic(), "*");
        // 设置每次拉取的最大消息数量
        subscribePullConsumer.setPullBatchSize(20);
        subscribePullConsumer.start();

        // 定时任务，完成任务后十秒再拉取一次
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, runnable -> new Thread(runnable));
        executor.scheduleWithFixedDelay(this::pullMessage, 1, 10, TimeUnit.SECONDS);
    }

    public void pullMessage() {
        List<MessageExt> messageExts = subscribePullConsumer.poll();
        LOGGER.info("subscribe lite consumer pull message: [{}]", messageExts.toString());
    }

}
