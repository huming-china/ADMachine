package com.fgecctv.trumpet.shell.network.mqtt.command;

public class Screenshot implements MqttCommand {
    public String action = "callback_affix";
    public String terminalId;
    public String type = "1";
    public String name;
    public String content;
    public String ip;
}
