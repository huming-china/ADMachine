package com.fgecctv.trumpet.shell.network.mqtt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.Serializable;

public class ConnectivityChangedReceiver extends BroadcastReceiver implements Serializable {
    private AndroidMqttClient androidMqttClient;

    public ConnectivityChangedReceiver(AndroidMqttClient androidMqttClient) {
        this.androidMqttClient = androidMqttClient;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!androidMqttClient.isConnected())
            androidMqttClient.connect();
    }
}
