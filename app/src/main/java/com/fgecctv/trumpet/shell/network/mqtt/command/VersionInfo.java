package com.fgecctv.trumpet.shell.network.mqtt.command;

public class VersionInfo implements MqttCommand{
    public String action = "VersionInfo";
    public String terminalId;
    public int versionCode;
    public String versionName;
}
