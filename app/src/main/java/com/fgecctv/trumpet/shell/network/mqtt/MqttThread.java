package com.fgecctv.trumpet.shell.network.mqtt;

import android.os.HandlerThread;

class MqttThread extends HandlerThread {
   MqttThread(String name) {
        super(name);
    }
}
