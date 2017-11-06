package com.fgecctv.trumpet.shell.network.mqtt.command;

import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.fgecctv.view.MarqueeView;

import java.util.concurrent.TimeUnit;

public class PublishNews implements MqttCommand {
    public String action;
    public String message;
    public int time;
    public String unit;
    public String speed;
    public int isAdd;
    public String fontColor;
    public int fontSize;
    public int position;
    public int transparency;
    public MarqueeView.Params to() {
        MarqueeView.Params params = new MarqueeView.Params();
        params.text = message;
        return params;
    }
}
