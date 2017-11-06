package com.fgecctv.trumpet.shell.network.mqtt;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.fgecctv.lang.ByteArray;
import com.fgecctv.trumpet.shell.BuildConfig;
import com.fgecctv.trumpet.shell.business.upload.OnUpLoadInfoCallBack;
import com.fgecctv.trumpet.shell.network.mqtt.command.Screenshot;
import com.fgecctv.trumpet.shell.network.mqtt.command.VersionInfo;
import com.squirrel.voyage.Voyage;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class AndroidMqttClient implements Serializable {
    private static final String TAG = "AndroidMqttClient";
    private static final Handler handler;

    private static AndroidMqttClient instance;

    static {
        MqttThread thread = new MqttThread("MqttThread");
        thread.start();

        handler = new Handler(thread.getLooper());
    }

    MqttClient mqttClient;
    MqttConnection mqttConnection;
    private Runnable connect = new Runnable() {
        @Override
        public void run() {
            try {
                mqttClient.connect(mqttConnection.mqttConnectOptions);
            } catch (MqttException e) {
                if (!isConnected()) {
                    Log.e(TAG, "Failed to connect mqtt server.");
                    handler.postDelayed(connect, TimeUnit.SECONDS.toMillis(3));
                }
            }
        }
    };

    private Runnable disconnect = new Runnable() {
        @Override
        public void run() {
            try {
                mqttClient.disconnect();
            } catch (MqttException e) {
                Voyage.e(TAG, "Failed to disconnect mqtt server.");
            }
        }
    };

    private AndroidMqttClient(MqttConnection connection) {
        this.mqttConnection = connection;
        try {
            mqttClient = new MqttClient(connection.uri, connection.clientId, new MemoryPersistence());
            mqttClient.setCallback(new MqttCallbackHandler(this));
        } catch (MqttException e) {
            Log.e(TAG, "Mqtt初始化失败");
        }
    }

    public static AndroidMqttClient getInstance(Context context, String id) {
        if (instance == null)
            instance = new AndroidMqttClient(new MqttConnection(id));
        return instance;
    }

    boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }

    void connect() {
        handler.post(connect);
    }

    public void publishScreenshot(Bitmap bitmap, OnUpLoadInfoCallBack callBack) {
        Screenshot screenshot = new Screenshot();
        screenshot.terminalId = mqttConnection.clientId;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 32, byteArrayOutputStream);
        Log.d(TAG, "上传截图System.currentTimeMillis():" + System.currentTimeMillis());
        screenshot.content = ByteArray.toHexString(byteArrayOutputStream.toByteArray());
        screenshot.name = System.currentTimeMillis() + ".jpeg";
        String payload = JSON.toJSONString(screenshot);
        MqttMessage mqttMessage = new MqttMessage(payload.getBytes());
        try {
            mqttClient.publish(mqttConnection.publishingTopic, mqttMessage);
        } catch (MqttException e) {
            Log.e(TAG, "上传截图失败");
            callBack.onUpLoadFail();
            return;
        }
        callBack.onUpLoadSuccess();
    }

    public void publishHeartbeatPackage() {
        MqttMessage mqttMessage = new MqttMessage("心跳包".getBytes());
        try {
            mqttClient.publish(mqttConnection.publishingTopic, mqttMessage);
        } catch (MqttException e) {
            Log.e(TAG, "发布心跳包失败");
        }
    }

    public void publishVersionInfo(OnUpLoadInfoCallBack callBack) {
        VersionInfo versionInfo = new VersionInfo();
        versionInfo.terminalId = mqttConnection.clientId;
        versionInfo.versionCode = BuildConfig.VERSION_CODE;
        versionInfo.versionName = BuildConfig.VERSION_NAME;
        String payload = JSON.toJSONString(versionInfo);
        MqttMessage mqttMessage = new MqttMessage(payload.getBytes());
        try {
            mqttClient.publish(mqttConnection.publishingTopic, mqttMessage);
        } catch (MqttException e) {
            Log.e(TAG, "发布版本信息失败");
            callBack.onUpLoadFail();
            return;
        }
        callBack.onUpLoadSuccess();
    }

    public void disconnect() {
        handler.post(disconnect);
    }
}