/*
 * Copyright (c) 2023年 by XuanWu Wireless Technology Co.Ltd.
 *             All rights reserved
 */
package com.ligouzi.controller;

import com.ligouzi.config.RocketmqConfig;
import com.ligouzi.dto.request.MessageReq;
import com.ligouzi.dto.response.JsonResp;
import com.ligouzi.producer.BatchMessageProducer;
import com.ligouzi.producer.CustomMessageProducer;
import com.ligouzi.producer.OrderMessageProducer;
import com.ligouzi.producer.ScheduledMessageProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yezehao
 * @date 2023-07-24
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private RocketmqConfig rocketmqConfig;

    @Resource
    private CustomMessageProducer customMessageProducer;

    @Resource
    private OrderMessageProducer orderMessageProducer;

    @Resource
    private ScheduledMessageProducer scheduledMessageProducer;

    @Resource
    private BatchMessageProducer batchMessageProducer;

    @PostMapping("/syncMessage")
    public JsonResp syncMessage(@RequestBody MessageReq req) throws Exception {
        Message message = new Message(rocketmqConfig.getCustomTopic(), req.getTag(), req.getKey(), req.getBody().getBytes());
        return JsonResp.success(customMessageProducer.syncSend(message));
    }

    @PostMapping("/asyncMessage")
    public JsonResp asyncMessage(@RequestBody MessageReq req) throws Exception {
        Message message = new Message(rocketmqConfig.getCustomTopic(), req.getTag(), req.getKey(), req.getBody().getBytes());
        customMessageProducer.asyncSend(message);
        return JsonResp.success();
    }

    @PostMapping("/onewayMessage")
    public JsonResp onewayMessage(@RequestBody MessageReq req) throws Exception {
        Message message = new Message(rocketmqConfig.getCustomTopic(), req.getTag(), req.getKey(), req.getBody().getBytes());
        customMessageProducer.sendOneway(message);
        return JsonResp.success();
    }

    @PostMapping("/orderMessage")
    public JsonResp orderMessage(@RequestBody MessageReq req) throws Exception {
        Message message = new Message(rocketmqConfig.getCustomTopic(), req.getTag(), req.getKey(), req.getBody().getBytes());
        SendResult sendResult = orderMessageProducer.orderSend(message, req.getSelectKey());
        return JsonResp.success(sendResult);
    }

    @PostMapping("/scheduledMessage")
    public JsonResp scheduledMessage(@RequestBody MessageReq req) throws Exception {
        Message message = new Message(rocketmqConfig.getCustomTopic(), req.getTag(), req.getKey(), req.getBody().getBytes());
        SendResult sendResult = scheduledMessageProducer.scheduledSend(message, req.getDelayTimeLevel());
        return JsonResp.success(sendResult);
    }

    @PostMapping("/batchMessage")
    public JsonResp batchMessage(@RequestBody List<MessageReq> list) throws Exception {
        List<Message> messages = list.stream()
            .map(req -> new Message(rocketmqConfig.getCustomTopic(), req.getTag(), req.getKey(), req.getBody().getBytes()))
            .collect(Collectors.toList());
        SendResult sendResult = batchMessageProducer.batchSend(messages);
        return JsonResp.success(sendResult);
    }

}
