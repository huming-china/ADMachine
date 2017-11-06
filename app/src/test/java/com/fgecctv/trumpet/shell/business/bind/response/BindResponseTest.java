package com.fgecctv.trumpet.shell.business.bind.response;

import com.alibaba.fastjson.JSON;
import com.fgecctv.trumpet.shell.network.http.response.BindResponse;

import org.junit.Assert;
import org.junit.Test;

public class BindResponseTest {
    @Test
    public void test() {
        BindResponse bindResponse = JSON.parseObject("{\"errno\":\"5\",\"error\":\"该终端已经绑定，请先登录后台解绑\"}", BindResponse.class);
        Assert.assertEquals("5", bindResponse.errorCode);
        Assert.assertEquals("该终端已经绑定，请先登录后台解绑", bindResponse.message);
    }
}