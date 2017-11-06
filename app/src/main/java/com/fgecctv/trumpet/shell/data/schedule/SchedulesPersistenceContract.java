package com.fgecctv.trumpet.shell.data.schedule;

class SchedulesPersistenceContract {
    static abstract class SchedulesEntry {
        static final String TABLE_NAME = "schedules";
        static final String COLUMN_NAME_ID = "_id";
        static final String COLUMN_NAME_DAY_OF_WEEK = "_dayOfWeek";
        static final String COLUMN_NAME_CLOSE_HOUR = "_closeHour";
        static final String COLUMN_NAME_CLOSE_MINUTE = "_closeMinute";
        static final String COLUMN_NAME_POWER_HOUR = "_powerHour";
        static final String COLUMN_NAME_POWER_MINUTE = "_powerMinute";
    }
}
