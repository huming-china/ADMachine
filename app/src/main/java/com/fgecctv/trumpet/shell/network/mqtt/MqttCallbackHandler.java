package com.fgecctv.trumpet.shell.network.mqtt;

import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fgecctv.trumpet.shell.network.mqtt.command.Advertise;
import com.fgecctv.trumpet.shell.network.mqtt.command.CancelAd;
import com.fgecctv.trumpet.shell.network.mqtt.command.CancelHeadline;
import com.fgecctv.trumpet.shell.network.mqtt.command.MonitorsCommand;
import com.fgecctv.trumpet.shell.network.mqtt.command.MqttCommand;
import com.fgecctv.trumpet.shell.network.mqtt.command.PublishNews;
import com.fgecctv.trumpet.shell.network.mqtt.command.RestartCommand;
import com.fgecctv.trumpet.shell.network.mqtt.command.ScheduleSettings;
import com.fgecctv.trumpet.shell.network.mqtt.command.SetVolumeCommand;
import com.squirrel.voyage.Voyage;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

class MqttCallbackHandler implements MqttCallbackExtended {

    private static final String TAG = "MqttCallbackHandler";

    private static final Map<String, Class<? extends MqttCommand>> map;

    static {
        map = new HashMap<>();
        map.put("sendMessage", PublishNews.class);
        map.put("emptyMessage", CancelHeadline.class);
        map.put("emptyProgram", CancelAd.class);
        map.put("publish", Advertise.class);
        map.put("setVolume", SetVolumeCommand.class);
        map.put("videoIpManager", MonitorsCommand.class);
        map.put("timingSwitch", ScheduleSettings.class);
        map.put("restart", RestartCommand.class);
    }

    private final WeakReference<AndroidMqttClient> androidMqttClientWeakReference;

    MqttCallbackHandler(AndroidMqttClient androidMqttClient) {
        androidMqttClientWeakReference = new WeakReference<>(androidMqttClient);
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.w(TAG, "Mqtt connection is lost.");
    }

    @Override
    public void messageArrived(@Nullable String topic, MqttMessage message) throws Exception {
        String content = new String(message.getPayload());

        try {
            MqttCommand command = toMqttCommand(content);
            EventBus.getDefault().post(command);
        } catch (Throwable t) {
            Voyage.e(TAG, content);
        }
    }

    private MqttCommand toMqttCommand(String jsonString) {
        Log.v(TAG, "Mqtt Message Arrived:" + jsonString);

        JSONObject jsonObject = (JSONObject) JSONObject.parse(jsonString);
        return JSON.parseObject(jsonString, map.get(jsonObject.getString("action")));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        AndroidMqttClient androidMqttClient = androidMqttClientWeakReference.get();
        if(androidMqttClient == null)
            return;

        try {
            androidMqttClient.mqttClient.subscribe(
                    androidMqttClient.mqttConnection.subscriptionTopic,
                    androidMqttClient.mqttConnection.qos);
        } catch (MqttException e) {
            throw new AssertionError();
        }
    }
}
