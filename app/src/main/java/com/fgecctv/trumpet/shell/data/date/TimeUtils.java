package com.fgecctv.trumpet.shell.data.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


 class TimeUtils {
     static long getLongTime(String time, String pattern) {
        long currentTime = 0;
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            Date date = format.parse(time);
            currentTime = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return currentTime;
    }
}
