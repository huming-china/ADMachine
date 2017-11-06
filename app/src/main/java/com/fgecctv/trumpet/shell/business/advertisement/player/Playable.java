package com.fgecctv.trumpet.shell.business.advertisement.player;

import android.graphics.Rect;
import android.net.Uri;
import android.support.annotation.NonNull;

public interface Playable {

    /**
     * The duration of the task.
     *
     * @return The time in millisecond the task lasts.
     */
    long getDuration();

    @NonNull
    Uri getUri();

    @NonNull
    Rect getFrameOfMonitorWindow();

    @NonNull
    String getId();

    boolean isExclusive();
}