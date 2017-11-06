package com.fgecctv.trumpet.shell.data.schedule;

public final class ScheduleRecord {
    public final String dayOfWeek;
    public final String powerHour;
    public final String powerMinute;
    public final String closeHour;
    public final String closeMinute;
    public final String id;

    private ScheduleRecord(String dayOfWeek, String powerHour, String powerMinute, String closeHour, String closeMinute, String id) {
        this.dayOfWeek = dayOfWeek;
        this.powerHour = powerHour;
        this.powerMinute = powerMinute;
        this.closeHour = closeHour;
        this.closeMinute = closeMinute;
        this.id = id;
    }

    ScheduleRecord(String dayOfWeek, String powerHour, String powerMinute, String closeHour, String closeMinute) {
        this(dayOfWeek, powerHour, powerMinute, closeHour, closeMinute, "");
    }
}
