/*
 * Copyright (c) 2023年 by XuanWu Wireless Technology Co.Ltd.
 *             All rights reserved
 */
package com.ligouzi.dto.response;

import lombok.Data;
import org.springframework.util.Assert;

/**
 * @author yezehao
 * @date 2023-07-24
 */
@Data
public class JsonResp {

    public static final int SUCCESS = 0;

    public static final int FAIL = -1;

    private int code;

    private Object data;

    private String msg;

    public JsonResp() {
    }

    public JsonResp(int code, Object data) {
        this.code = code;
        this.data = data;
    }

    public JsonResp(int code, Object data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static JsonResp success() {
        return new JsonResp(SUCCESS, null);
    }

    public static JsonResp success(Object data) {
        return new JsonResp(SUCCESS, data);
    }

    public static JsonResp fail(String message) {
        return fail(FAIL, message);
    }

    public static JsonResp fail(int code, String message) {
        Assert.isTrue(code != SUCCESS, "Must be not success code: " + code);
        return new JsonResp(code, null, message);
    }

}
