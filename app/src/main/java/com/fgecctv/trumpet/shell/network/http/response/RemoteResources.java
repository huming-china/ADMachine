package com.fgecctv.trumpet.shell.network.http.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.fgecctv.trumpet.shell.data.ad.Repeat;
import com.squirrel.voyage.Voyage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RemoteResources {

    private static final String TAG = "RemoteResources";

    public String action;
    public String errno;
    public String terminalId;
    public String tm;

    @JSONField(name = "programList")
    public List<Ad> ads = new ArrayList<>();

    @JSONField(name = "timeList")
    public List<TimeListBean> timeList;

    @JSONField(name = "vmIpList")
    public List<Monitor> monitors;

    public static class Ad {
        public String id;
        public String url;
        public String timestamp;
        public String priority;
        public String effectiveDate;
        public String expiryDate;
        public List<Repeat> repeats = new ArrayList<>();
        private DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        public long getTimestamp() {
            try {
                return format.parse(timestamp).getTime();
            } catch (ParseException e) {
                Voyage.e(TAG, "Invalid timestamp date: " + timestamp);
                return System.currentTimeMillis();
            }
        }

        public long getPriority() {
            try {
                return format.parse(priority).getTime();
            } catch (ParseException e) {
                Voyage.e(TAG, "Invalid priority date: " + priority);
                return System.currentTimeMillis();
            }
        }

        public List<Repeat> getRepeats() {
            return repeats;
        }

        public long getEffectiveDate() {
            try {
                return format.parse(effectiveDate).getTime();
            } catch (ParseException e) {
                Voyage.e(TAG, "Invalid effective date: " + effectiveDate);
                return System.currentTimeMillis();
            }
        }

        public long getExpiryDate() {
            try {
                return format.parse(expiryDate).getTime();
            } catch (ParseException e) {
                Voyage.e(TAG, "Invalid expiry date: " + expiryDate);
                return System.currentTimeMillis();
            }
        }
    }

    public static class TimeListBean {
        public String closeHour;
        public String closeMinute;
        public String powerHour;
        public String powerMinute;
        public String week;
    }

    public static class Monitor {
        public int delayTime;
        public String ip;
        public String packetName;
        public int transitTime;
    }
}
