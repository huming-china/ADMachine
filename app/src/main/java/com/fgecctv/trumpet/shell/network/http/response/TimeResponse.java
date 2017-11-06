package com.fgecctv.trumpet.shell.network.http.response;

import com.alibaba.fastjson.annotation.JSONField;

public class TimeResponse {
    @JSONField(name = "errno")
    public String errorCode;
    @JSONField(name = "error")
    public String message;
    @JSONField(name = "zone")
    public String zone;
    @JSONField(name = "date")
    public String data;

    public static class Date {
        public String zone;
        public String time;
    }
}
