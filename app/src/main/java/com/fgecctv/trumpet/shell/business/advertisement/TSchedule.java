package com.fgecctv.trumpet.shell.business.advertisement;

import com.fgecctv.trumpet.shell.data.schedule.ScheduleRecord;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

class TSchedule {
    public static final long MILLIS_OF_ONE_DAY = 24 * 60 * 60 * 1000;

    public static long getLatestPowerOnMillis(List<ScheduleRecord> list) {
        List<Long> powerOnMillis = new ArrayList<>();
        for (ScheduleRecord record : list) {
            Calendar calendar = Calendar.getInstance();

            if (calendar.get(Calendar.DAY_OF_WEEK) == getDayOfWeek(record.dayOfWeek)) {
                List<ScheduleRecord> currentDaySchedules = new ArrayList<>();
                currentDaySchedules.add(record);
                //服务器发送的列表中周几与当前一样，则比较时间，如果当前时间在列表时间的后面，则把该时间设置为开机时间
                for (ScheduleRecord s : currentDaySchedules) {
                    if ((Integer.parseInt(s.powerHour) == calendar.get(Calendar.HOUR_OF_DAY)
                            && Integer.parseInt(s.powerMinute) > calendar.get(Calendar.MINUTE))
                            || Integer.parseInt(s.powerHour) > calendar.get(Calendar.HOUR_OF_DAY))
                        powerOnMillis.add(getCurrentDayPowerOnMillis(s));
                }
            }
            //把明天当作第一天，依次往后比到下周的今天，找到开机时间最小毫秒值
            for (int i = 1; i < 8; i++) {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, i);
                if (c.get(Calendar.DAY_OF_WEEK) == getDayOfWeek(record.dayOfWeek)) {
                    List<ScheduleRecord> records = new ArrayList<>();
                    records.add(record);
                    for (ScheduleRecord s : records) {
                        powerOnMillis.add(getCurrentDayPowerOnMillis(s) + MILLIS_OF_ONE_DAY * i);
                    }
                }
            }
        }
        if (!powerOnMillis.isEmpty()) {
            return Collections.min(powerOnMillis);
        }
        return 0L;
    }

    public static long getLatestPowerOffMillis(List<ScheduleRecord> list) {
        List<Long> powerOffMillis = new ArrayList<>();
        for (ScheduleRecord record : list) {
            Calendar calendar = Calendar.getInstance();

            if (calendar.get(Calendar.DAY_OF_WEEK) == getDayOfWeek(record.dayOfWeek)) {
                List<ScheduleRecord> currentDaySchedules = new ArrayList<>();
                currentDaySchedules.add(record);
                //服务器发送的列表中周几与当前一样，则比较时间，如果当前时间在列表时间的后面，则把该时间设置为关机时间
                for (ScheduleRecord s : currentDaySchedules) {
                    if ((Integer.parseInt(s.closeHour) == calendar.get(Calendar.HOUR_OF_DAY)
                            && Integer.parseInt(s.closeMinute) > calendar.get(Calendar.MINUTE))
                            || Integer.parseInt(s.closeHour) > calendar.get(Calendar.HOUR_OF_DAY))
                        powerOffMillis.add(getCurrentDayPowerOffMillis(s));
                }
            }

            //把明天当作第一天，依次往后比到下周的今天，找到关机时间最小毫秒值
            for (int i = 1; i < 8; i++) {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, i);
                if (c.get(Calendar.DAY_OF_WEEK) == getDayOfWeek(record.dayOfWeek)) {
                    List<ScheduleRecord> records = new ArrayList<>();
                    records.add(record);
                    for (ScheduleRecord s : records) {
                        powerOffMillis.add(getCurrentDayPowerOffMillis(s) + MILLIS_OF_ONE_DAY * i);
                    }
                }
            }
        }
        if (!powerOffMillis.isEmpty()) {
            return Collections.min(powerOffMillis);
        }
        return 0L;
    }

    private static int getDayOfWeek(String day) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("Sunday", 1);
        map.put("Monday", 2);
        map.put("Tuesday", 3);
        map.put("Wednesday", 4);
        map.put("Thursday", 5);
        map.put("Friday", 6);
        map.put("Saturday", 7);
        return map.get(day);
    }

    private static long getCurrentDayPowerOnMillis(ScheduleRecord record) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(record.powerHour));
        calendar.set(Calendar.MINUTE, Integer.parseInt(record.powerMinute));
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    private static long getCurrentDayPowerOffMillis(ScheduleRecord record) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(record.closeHour));
        calendar.set(Calendar.MINUTE, Integer.parseInt(record.closeMinute));
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }
}
