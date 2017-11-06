package com.fgecctv.trumpet.shell.data.ad.repository;

import android.content.res.XmlResourceParser;
import android.graphics.Rect;
import android.util.Log;
import android.util.Xml;

import com.fgecctv.io.Files;
import com.fgecctv.trumpet.shell.BuildConfig;
import com.fgecctv.trumpet.shell.app.AppEnvironment;
import com.fgecctv.trumpet.shell.data.ad.Repeat;
import com.squirrel.voyage.Voyage;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Ad {

    private static final String TAG = "Ad";

    private String id;
    private String uri = "file:///android_asset/blank.html";
    private Rect monitorWindowLocation = new Rect();
    private long duration = TimeUnit.SECONDS.toSeconds(30);
    private long timestamp;
    private long priority;
    private long expiryDate = Long.MAX_VALUE;
    private long effectiveDate;
    private List<Repeat> repeats = getDefaultRepeats();

    private List<Repeat> getDefaultRepeats() {
        ArrayList<Repeat> repeats = new ArrayList<>(1);
        Repeat range = new Repeat(0, TimeUnit.DAYS.toMillis(1) - 1);
        repeats.add(range);
        return repeats;
    }

    public String getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    public Rect getMonitorWindowLocation() {
        return new Rect(monitorWindowLocation);
    }

    public long getDuration() {
        return duration;
    }

    public long getPriority() {
        return priority;
    }

    public boolean isExclusive() {
        return getPriority() >= 2514038400000L;
    }

    public long getEffectiveDate() {
        return effectiveDate;
    }

    public long getExpiryDate() {
        return expiryDate;
    }

    public List<Repeat> getRepeats() {
        return new ArrayList<>(repeats);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void delete() {
        Files.delete(getDirectory());
    }

    private String getDirectory() {
        return AppEnvironment.getExternalStoragePath() +
                File.separator + "Ads" +
                File.separator + id;
    }

    private File getResourceFile() {
        return new File(getDirectory() + File.separator + "resource.xml");
    }

    public static class Builder {

        private Ad ad;

        public Builder(String id) {
            ad = new Ad();
            ad.id = id;
            readConfigurationFile();
        }

        public Builder() {
            ad = new Ad();
        }

        Builder(AdRecord record) {
            ad = new Ad();
            ad.id = record.getId();
            ad.uri = record.getUri();
            ad.priority = record.getPriority();
            ad.duration = record.getDuration();
            ad.effectiveDate = record.getEffectiveDate();
            ad.expiryDate = record.getExpiryDate();
            ad.monitorWindowLocation = record.getMonitorWindowLocation();
            ad.repeats = record.getRepeats();
            ad.timestamp = record.getTimestamp();
        }

        Builder setId(String id) {
            ad.id = id;
            return this;
        }

        private Builder setUri(String uri) {
            ad.uri = uri;
            return this;
        }

        Builder setTimestamp(long timestamp) {
            ad.timestamp = timestamp;
            return this;
        }

        private Builder setDuration(long duration) {
            ad.duration = duration;
            return this;
        }

        private Builder setMonitorWindowLocation(Rect monitorWindowLocation) {
            ad.monitorWindowLocation = monitorWindowLocation;
            return this;
        }

        public Builder setPriority(long priority) {
            ad.priority = priority;
            return this;
        }

        Builder setEffectiveDate(long effectiveDate) {
            ad.effectiveDate = effectiveDate;
            return this;
        }

        Builder setExpiryDate(long expiryDate) {
            ad.expiryDate = expiryDate;
            return this;
        }

        Builder setRepeats(List<Repeat> repeats) {
            ad.repeats = repeats;
            return this;
        }

        public Ad create() {
            return ad;
        }

        private void readConfigurationFile() {
            File file = ad.getResourceFile();
            InputStream inputStream;

            try {
                inputStream = new FileInputStream(file);
                XmlPullParser xrp = Xml.newPullParser();
                xrp.setInput(inputStream, "UTF-8");
                while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                    // 如果遇到了开始标签
                    if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                        String tagName = xrp.getName();// 获取标签的名字
                        switch (tagName) {
                            case "Place":
                                float left = Float.valueOf(xrp.getAttributeValue(null, "left").replaceAll("[a-z]*", ""));
                                float width = Float.valueOf(xrp.getAttributeValue(null, "width").replaceAll("[a-z]*", ""));
                                float height = Float.valueOf(xrp.getAttributeValue(null, "height").replaceAll("[a-z]*", ""));
                                float top = Float.valueOf(xrp.getAttributeValue(null, "top").replaceAll("[a-z]*", ""));
                                Rect rect = new Rect((int) left, (int) top, (int) (left + width), (int) (top + height));
                                setMonitorWindowLocation(rect);
                                break;
                            case "StayTime":
                                long duration = TimeUnit.SECONDS.toMillis(Integer.valueOf(xrp.nextText()));
                                setDuration(duration);
                                break;
                            case "url":
                                String uri = new File(file.getParent(), xrp.nextText()).toURI().toString();
                                setUri(uri);
                                break;
                        }
                    }
                    xrp.next();
                }
                inputStream.close();
            } catch (XmlPullParserException e) {
                Log.w(TAG, "Fail to parse " + file.getAbsolutePath());
            } catch (IOException e) {
                if (BuildConfig.DEBUG) throw new AssertionError("广告读取或者解析出错");
                Voyage.e(TAG, "后台又改接口了");
            }
        }
    }
}