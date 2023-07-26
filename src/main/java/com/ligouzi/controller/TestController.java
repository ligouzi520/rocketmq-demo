/*
 * Copyright (c) 2023年 by XuanWu Wireless Technology Co.Ltd.
 *             All rights reserved
 */
package com.ligouzi.controller;

import com.ligouzi.config.RocketmqConfig;
import com.ligouzi.dto.response.JsonResp;
import com.ligouzi.producer.CustomMessageProducer;
import com.ligouzi.producer.OrderMessageProducer;
import com.ligouzi.producer.ScheduledMessageProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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

    @GetMapping("/syncMessage")
    public JsonResp syncMessage(String tag, String key, String body) throws Exception {
        return JsonResp.success(customMessageProducer.syncSend(rocketmqConfig.getCustomTopic(), tag, key, body));
    }

    @GetMapping("/asyncMessage")
    public JsonResp asyncMessage(String tag, String key, String body) throws Exception {
        customMessageProducer.asyncSend(rocketmqConfig.getCustomTopic(), tag, key, body);
        return JsonResp.success();
    }

    @GetMapping("/onewayMessage")
    public JsonResp onewayMessage(String tag, String key, String body) throws Exception {
        customMessageProducer.sendOneway(rocketmqConfig.getCustomTopic(), tag, key, body);
        return JsonResp.success();
    }

    @GetMapping("/orderMessage")
    public JsonResp orderMessage(String tag, String key, String body, Integer selectKey) throws Exception {
        SendResult sendResult = orderMessageProducer.orderSend(rocketmqConfig.getCustomTopic(), tag, key, body, selectKey);
        return JsonResp.success(sendResult);
    }

    @GetMapping("/scheduledMessage")
    public JsonResp scheduledMessage(String tag, String key, String body, Integer delayTimeLevel) throws Exception {
        SendResult sendResult = scheduledMessageProducer.scheduledSend(rocketmqConfig.getCustomTopic(), tag, key, body, delayTimeLevel);
        return JsonResp.success(sendResult);
    }

}
