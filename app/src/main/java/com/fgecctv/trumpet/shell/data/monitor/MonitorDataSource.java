package com.fgecctv.trumpet.shell.data.monitor;

import java.util.List;

interface MonitorDataSource {

    void insert(List<MonitorRecord> records);

    List<MonitorRecord> query();

    void deleteAll();

}
