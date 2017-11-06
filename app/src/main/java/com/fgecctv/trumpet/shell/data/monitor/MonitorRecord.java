package com.fgecctv.trumpet.shell.data.monitor;

class MonitorRecord {
    public String ip;
    String group;
    int delayTime;
    int transitTime;

    private MonitorRecord() {
    }

    public static class Builder {

        MonitorRecord record = new MonitorRecord();

        Builder setIp(String ip) {
            record.ip = ip;
            return this;
        }

        Builder setDuration(int duration) {
            record.transitTime = duration;
            return this;
        }

        MonitorRecord create() {
            return record;
        }
    }
}
