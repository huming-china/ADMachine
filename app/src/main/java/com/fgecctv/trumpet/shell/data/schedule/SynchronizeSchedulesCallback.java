package com.fgecctv.trumpet.shell.data.schedule;

import java.util.List;

public interface SynchronizeSchedulesCallback {
    void onSchedulesSynchronized(List<ScheduleRecord> records);

    void onRemoteDataNotAvailable(List<ScheduleRecord> records);
}
