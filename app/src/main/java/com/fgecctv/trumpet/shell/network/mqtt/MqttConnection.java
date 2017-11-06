package com.fgecctv.trumpet.shell.network.mqtt;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

class MqttConnection {
    public final String uri;
    final String clientId;
    final int qos = 2;
    final String subscriptionTopic;
    final String publishingTopic;
    final MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
    private final int port = 1885;
    private String host = "lamp.cloudring.net";
    private boolean sslConnection = false;
    private String userName = "admin";
    private char[] password = "123456".toCharArray();

    MqttConnection(String clientId) {
        this.clientId = clientId;
        this.uri = sslConnection ? "ssl://" : "tcp://" + host + ":" + port;
        this.mqttConnectOptions.setCleanSession(false);
        this.mqttConnectOptions.setUserName(userName);
        this.mqttConnectOptions.setPassword(password);
        this.mqttConnectOptions.setAutomaticReconnect(true);
        this.subscriptionTopic = String.format("cloudringAd/client/terminal/%s/in", clientId);
        this.publishingTopic = String.format("cloudringAd/server/terminal/%s", clientId);
    }
}
