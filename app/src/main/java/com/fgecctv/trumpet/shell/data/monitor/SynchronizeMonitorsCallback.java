package com.fgecctv.trumpet.shell.data.monitor;

public interface SynchronizeMonitorsCallback {

    void onMonitorsSynchronized(Monitors monitors);

    void onRemoteDataNotAvailable(Monitors monitors);
}
