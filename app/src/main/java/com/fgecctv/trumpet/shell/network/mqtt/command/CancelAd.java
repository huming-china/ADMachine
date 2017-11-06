package com.fgecctv.trumpet.shell.network.mqtt.command;

public class CancelAd implements MqttCommand {
    public String action;
    public String id;

    public String[] getIds() {
        return id.split(",");
    }
}
