package com.fgecctv.trumpet.shell.business.advertisement.player;

import android.graphics.Rect;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


class AdTask implements Playable {

    long duration;

    Uri uri = Uri.parse("file:///android_asset/blank.html");

    Rect frame = new Rect();

    boolean isExclusive = false;

    String id;

    @Override
    public long getDuration() {
        return duration;
    }

    @NonNull
    @Override
    public Uri getUri() {
        return uri;
    }

    @NonNull
    @Override
    public Rect getFrameOfMonitorWindow() {
        return frame;
    }

    @Override
    public boolean isExclusive() {
        return isExclusive;
    }

    @NonNull
    public String getId() {
        return id;
    }

}
