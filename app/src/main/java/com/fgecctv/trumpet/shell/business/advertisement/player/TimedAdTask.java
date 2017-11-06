package com.fgecctv.trumpet.shell.business.advertisement.player;


import android.graphics.Rect;
import android.net.Uri;
import android.support.annotation.NonNull;

import static com.google.common.base.Preconditions.checkArgument;

class TimedAdTask extends AdTask {

    private long startTime;

    private long endTime;

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public static class Builder {
        private TimedAdTask task = new TimedAdTask();

        Builder setStartTime(long startTime) {
            task.startTime = startTime;
            return this;
        }

        Builder setEndTime(long endTime) {
            task.endTime = endTime;
            return this;
        }

        Builder setId(@NonNull String id) {
            task.id = id;
            return this;
        }

        Builder setUri(Uri uri) {
            task.uri = uri;
            return this;
        }

        Builder setDuration(long duration) {
            task.duration = duration;
            return this;
        }

        Builder setExclusive(boolean exclusive) {
            task.isExclusive = exclusive;
            return this;
        }

        Builder setFrameOfMonitorWindow(Rect frame) {
            task.frame = new Rect(frame);
            return this;
        }

        public TimedAdTask create() {
            checkArgument(task.startTime <= task.endTime,
                    "startTime(" + task.startTime + ") should be less than endTime(" + task.endTime + ")");
            return task;
        }
    }
}