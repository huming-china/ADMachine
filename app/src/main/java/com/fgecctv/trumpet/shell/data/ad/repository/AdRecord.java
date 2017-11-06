package com.fgecctv.trumpet.shell.data.ad.repository;

import android.graphics.Rect;
import android.support.annotation.Nullable;

import com.fgecctv.trumpet.shell.data.ad.Repeat;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

class AdRecord {
    private String id = "";
    private String uri = "file:///android_asset/blank.html";
    private long duration = TimeUnit.SECONDS.toSeconds(30);
    private Rect monitorWindowLocation = new Rect();
    private long priority;
    private long effectiveDate;
    private long expiryDate = Long.MAX_VALUE;
    private List<Repeat> repeats = getDefaultRepeats();
    private long timestamp;

    private List<Repeat> getDefaultRepeats() {
        ArrayList<Repeat> repeats = new ArrayList<>(1);
        Repeat repeat = new Repeat(0, TimeUnit.DAYS.toMillis(1) - 1);
        repeats.add(repeat);
        return repeats;
    }

    public String getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    long getDuration() {
        return duration;
    }

    Rect getMonitorWindowLocation() {
        return new Rect(monitorWindowLocation);
    }

    long getPriority() {
        return priority;
    }

    long getEffectiveDate() {
        return effectiveDate;
    }

    long getExpiryDate() {
        return expiryDate;
    }

    List<Repeat> getRepeats() {
        return new ArrayList<>(repeats);
    }

    long getTimestamp() {
        return timestamp;
    }

    static class Builder {

        private AdRecord adRecord = new AdRecord();

        Builder() {
        }

        Builder(Ad ad) {
            adRecord.id = ad.getId();
            adRecord.uri = ad.getUri();
            adRecord.duration = ad.getDuration();
            adRecord.effectiveDate = ad.getEffectiveDate();
            adRecord.expiryDate = ad.getExpiryDate();
            adRecord.monitorWindowLocation = ad.getMonitorWindowLocation();
            adRecord.priority = ad.getPriority();
            adRecord.repeats = ad.getRepeats();
            adRecord.timestamp = ad.getTimestamp();
        }

        public Builder setId(String id) {
            adRecord.id = id;
            return this;
        }

        public Builder setUri(String uri) {
            adRecord.uri = uri;
            return this;
        }

        Builder setDuration(long duration) {
            adRecord.duration = duration;
            return this;
        }

        Builder setMonitorWindowLocation(Rect rect) {
            adRecord.monitorWindowLocation = rect == null ? new Rect() : rect;
            return this;
        }

        Builder setPriority(long priority) {
            adRecord.priority = priority;
            return this;
        }

        Builder setEffectiveDate(long effectiveDate) {
            adRecord.effectiveDate = effectiveDate;
            return this;
        }

        Builder setExpiryDate(long expiryDate) {
            adRecord.expiryDate = expiryDate;
            return this;
        }

        Builder setRepeats(@Nullable List<Repeat> repeats) {
            adRecord.repeats = repeats == null || repeats.isEmpty() ? adRecord.getDefaultRepeats() : repeats;
            return this;
        }

        Builder setTimestamp(long timestamp) {
            adRecord.timestamp = timestamp;
            return this;
        }

        public AdRecord create() {
            Preconditions.checkArgument(adRecord.effectiveDate <= adRecord.expiryDate);
            return adRecord;
        }
    }
}