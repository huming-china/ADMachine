package com.fgecctv.hardware;

import android.os.Handler;
import android.os.SystemClock;

import com.alibaba.fastjson.JSONObject;
import com.fgecctv.ipc_client.IpcPresenter;
import com.squirrel.voyage.Voyage;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Power {
    private static final String TAG = "Power";

    static {
        System.loadLibrary("rtc-lib");
    }

    public static void onAtTime(long timeInMillis) {

         Calendar calendar = Calendar.getInstance();
         long targetTime = timeInMillis- TimeZone.getDefault().getRawOffset();
         calendar.setTimeInMillis(targetTime);

        if (calendar.get(Calendar.YEAR) > 1900)
            enable();

        else
            disable();

        setNextTurnOnTime(
                calendar.get(Calendar.YEAR) - 1900,
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE),
                calendar.get(Calendar.DAY_OF_WEEK) > 1 ? calendar.get(Calendar.DAY_OF_WEEK) - 1 : 0,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE));
        Voyage.e(TAG, "开机时间:" + calendar.getTime());
    }

    public static native void enable();

    private static native void disable();

    public static void offAtTime(long timeInMillis) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                off();
            }
        }, timeInMillis - System.currentTimeMillis());

        Voyage.e(TAG, "关机时间:" + new Date(timeInMillis));
    }

    public static native int setNextTurnOnTime(int year,
                                               int month,
                                               int date,
                                               int dayOfWeek,
                                               int hourOfDay,
                                               int minute);

    private static native void off();

    public static void restart(final IpcPresenter ipcPresenter) {
        Thread thread = new Thread(){
            @Override
            public void run() {
                SystemClock.sleep(500);
                JSONObject object = new JSONObject();
                object.put("type","reboot");
                ipcPresenter.sendMessage(object.toString());
            }
        };
        thread.start();
    }

}