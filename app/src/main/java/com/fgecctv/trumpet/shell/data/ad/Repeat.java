package com.fgecctv.trumpet.shell.data.ad;

import com.google.common.base.Preconditions;

import java.util.concurrent.TimeUnit;

public class Repeat {
    public long starts;
    public long ends;

    public Repeat() {
    }

    public Repeat(long start, long end) {
        Preconditions.checkArgument(start <= end);
        Preconditions.checkArgument(0 <= start && start < TimeUnit.DAYS.toMillis(1));
        Preconditions.checkArgument(0 <= end && end < TimeUnit.DAYS.toMillis(1));

        this.starts = start;
        this.ends = end;
    }
}