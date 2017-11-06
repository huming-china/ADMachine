package com.fgecctv.trumpet.shell.network.mqtt.command;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class ScheduleSettings implements MqttCommand {
    public String action = "timingSwitch";

    @JSONField(name = "list")
    public List<Schedule> schedules;

    public static class Schedule {
        @JSONField(name = "week")
        public String dayOfWeek;
        @JSONField(name = "powerHour")
        public String hourOfPowerOn;
        @JSONField(name = "powerMinute")
        public String minuteOfPowerOn;
        @JSONField(name = "closeHour")
        public String hourOfPowerOff;
        @JSONField(name = "closeMinute")
        public String minuteOfPowerOff;
    }
}
