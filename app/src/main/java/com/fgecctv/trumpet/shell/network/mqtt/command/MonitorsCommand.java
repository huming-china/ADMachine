package com.fgecctv.trumpet.shell.network.mqtt.command;

import java.util.List;

public class MonitorsCommand implements MqttCommand {

    public String action;
    public String method;
    public int switchingTime;
    public List<ListBean> list;

    public static class ListBean {
        public String ipGroup;
        public int delayTime;
        public int transitTime;
        public List<IpsBean> ips;

        public static class IpsBean {
            public String ip;
            public String oldIP;
        }
    }
}
