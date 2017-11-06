package com.fgecctv.trumpet.shell.network.http.response;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

public class BindResponse {
    @JSONField(name = "errno")
    public String errorCode;
    @JSONField(name = "error")
    public String message;

    private BindResponse() {
    }

    public static BindResponse createFromJson(String json) {
        return JSON.parseObject(json, BindResponse.class);
    }

    public boolean success() {
        return errorCode.equals("0");
    }
}
