package com.fgecctv.trumpet.shell.data.monitor;

import android.provider.BaseColumns;

class MonitorsPersistenceContract {
        static abstract class MonitorEntry implements BaseColumns {
        static final String TABLE_NAME = "monitor";
        static final String COLUMN_NAME_IP = "_ip";
        static final String COLUMN_NAME_GROUP = "_group";
        static final String COLUMN_NAME_DELAY_TIME = "_delayTime";
        static final String COLUMN_NAME_TRANSIT_TIME = "_transitTime";
    }
}
