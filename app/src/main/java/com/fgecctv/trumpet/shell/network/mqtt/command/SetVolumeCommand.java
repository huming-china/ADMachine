package com.fgecctv.trumpet.shell.network.mqtt.command;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Willard on 2016/3/21.
 */
public class SetVolumeCommand implements MqttCommand {
    public String action;
    @JSONField(name = "val")
    public int volume;
}
