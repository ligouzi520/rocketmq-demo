/*
 * Copyright (c) 2023年 by XuanWu Wireless Technology Co.Ltd.
 *             All rights reserved
 */
package com.ligouzi.consumer;

import com.ligouzi.config.RocketmqConfig;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yezehao
 * @date 2023-07-28
 */
@Component
public class AssignPullMessageConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssignPullMessageConsumer.class);

    private final DefaultLitePullConsumer assignPullConsumer;

    /**
     * 与Subscribe模式不同的是，Assign模式下没有自动的负载均衡机制，需要用户自行指定需要拉取的队列。
     */
    public AssignPullMessageConsumer(RocketmqConfig rocketmqConfig) throws MQClientException {
        assignPullConsumer = new DefaultLitePullConsumer(rocketmqConfig.getAssignPullConsumerGroup());
        // 关闭自动提交位点
        assignPullConsumer.setAutoCommit(false);
        assignPullConsumer.setNamesrvAddr(rocketmqConfig.getNameServer());
        assignPullConsumer.start();
        // 指定获取Topic队列
        Collection<MessageQueue> mqSet = assignPullConsumer.fetchMessageQueues(rocketmqConfig.getCustomTopic());
        List<MessageQueue> assignList = new ArrayList<>(mqSet).subList(0, mqSet.size() / 2);
        // 将特定的消费队列分配到消费者，像上述代码就是分配了指定Topic消息队列的前一半给消费者
        assignPullConsumer.assign(assignList);
        // 将消费者定位到指定的消息队列和偏移量
        assignPullConsumer.seek(assignList.get(0), 1);

        // 定时任务，完成任务后十秒再拉取一次
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, runnable -> new Thread(runnable));
        executor.scheduleWithFixedDelay(this::pullMessage, 1, 10, TimeUnit.SECONDS);
    }

    public void pullMessage() {
        List<MessageExt> messageExts = assignPullConsumer.poll();
        LOGGER.info("assign lite consumer pull message: [{}]", messageExts.toString());
        assignPullConsumer.commitSync();
    }

}
