package com.fgecctv.trumpet.shell.network.mqtt;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Locale;

public class AndroidMqttClientTest {
    @Test
    public void testStringFormat() {
        String result = String.format(Locale.ENGLISH, "%.0f%%", 100 * 0.56334);
        Assert.assertEquals("56%", result);
    }
}