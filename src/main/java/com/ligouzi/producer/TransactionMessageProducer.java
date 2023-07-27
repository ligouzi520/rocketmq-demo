/*
 * Copyright (c) 2023年 by XuanWu Wireless Technology Co.Ltd.
 *             All rights reserved
 */
package com.ligouzi.producer;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

/**
 * 事务消息生产者
 * @author yezehao
 * @date 2023-07-27
 */
@Component
public class TransactionMessageProducer {

    public class TransactionListenerImpl implements TransactionListener {

        /**
         * 半事务消息发送成功后，执行本地事务的方法，具体执行完本地事务后，可以在该方法中返回以下三种状态：
         * LocalTransactionState.COMMIT_MESSAGE：提交事务，允许消费者消费该消息
         * LocalTransactionState.ROLLBACK_MESSAGE：回滚事务，消息将被丢弃不允许消费。
         * LocalTransactionState.UNKNOW：暂时无法判断状态，等待固定时间以后Broker端根据回查规则向生产者进行消息回查。
         * @param msg 半事务消息
         * @param arg 自定义业务参数
         * @return 事务状态
         */
        @Override
        public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
            return null;
        }

        /**
         * 二次确认消息没有收到，Broker端回查事务状态的方法。
         * 回查规则：
         * 本地事务执行完成后，若Broker端收到的本地事务返回状态为LocalTransactionState.UNKNOW，或生产者应用退出导致本地事务未提交任何状态。
         * 则Broker端会向消息生产者发起事务回查，第一次回查后仍未获取到事务状态，则之后每隔一段时间会再次回查。
         * @param msg 检查消息
         * @return 事务状态
         */
        @Override
        public LocalTransactionState checkLocalTransaction(MessageExt msg) {
            return null;
        }
    }

}
