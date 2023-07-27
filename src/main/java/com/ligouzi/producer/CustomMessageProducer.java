/*
 * Copyright (c) 2023年 by XuanWu Wireless Technology Co.Ltd.
 *             All rights reserved
 */
package com.ligouzi.producer;

import com.ligouzi.config.RocketmqConfig;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 普通消息生产者
 * @author yezehao
 * @date 2023-07-24
 */
@Component
public class CustomMessageProducer extends AbstractMessageProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomMessageProducer.class);

    private static final String PRODUCER_TYPE = "custom";

    public CustomMessageProducer(RocketmqConfig rocketmqConfig) {
        super(rocketmqConfig.getCustomProducerGroup(), rocketmqConfig, PRODUCER_TYPE);
    }

    /**
     * 同步发送
     * 同步发送方式请务必捕获发送异常，并做业务侧失败兜底逻辑，如果忽略异常则可能会导致消息未成功发送的情况。
     */
    public SendResult syncSend(Message message) throws Exception {
        try {
            return this.producer.send(message);
        } catch (Exception e) {
            LOGGER.error("sync send message to rocketmq error, cause by: ", e);
            throw e;
        }
    }

    /**
     * 异步发送
     * 异步发送需要实现异步发送回调接口（SendCallback）。
     * 异步发送与同步发送代码唯一区别在于调用send接口的参数不同，异步发送不会等待发送返回。
     * 取而代之的是send方法需要传入 SendCallback 的实现，SendCallback 接口主要有onSuccess 和 onException 两个方法，表示消息发送成功和消息发送失败。
     */
    public void asyncSend(Message message) throws Exception {
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

    /**
     * 单向模式发送
     * 单向模式调用sendOneway，不会对返回结果有任何等待和处理。
     */
    public void sendOneway(Message message) throws Exception {
        try {
            this.producer.sendOneway(message);
        } catch (Exception e) {
            LOGGER.error("send oneway message to rocketmq error, cause by: ", e);
            throw e;
        }
    }

}
