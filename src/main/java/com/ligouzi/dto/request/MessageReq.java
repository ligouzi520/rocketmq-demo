/*
 * Copyright (c) 2023年 by XuanWu Wireless Technology Co.Ltd.
 *             All rights reserved
 */
package com.ligouzi.dto.request;

import lombok.Data;

/**
 * @author yezehao
 * @date 2023-07-27
 */
@Data
public class MessageReq {

    private String tag;

    private String key;

    private String body;

    private Integer selectKey;

    private Integer delayTimeLevel;

}
