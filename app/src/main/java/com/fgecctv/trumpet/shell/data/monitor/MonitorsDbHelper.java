package com.fgecctv.trumpet.shell.data.monitor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.fgecctv.trumpet.shell.data.monitor.MonitorsPersistenceContract.MonitorEntry;

class MonitorsDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "Monitors.db";

    private static final String TEXT_TYPE = " TEXT";

    private static final String BOOLEAN_TYPE = " INTEGER";

    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " +
            MonitorEntry.TABLE_NAME + " (" +
            MonitorEntry.COLUMN_NAME_IP + TEXT_TYPE + " PRIMARY KEY" + COMMA_SEP +
            MonitorEntry.COLUMN_NAME_GROUP + TEXT_TYPE + COMMA_SEP +
            MonitorEntry.COLUMN_NAME_DELAY_TIME + BOOLEAN_TYPE + COMMA_SEP +
            MonitorEntry.COLUMN_NAME_TRANSIT_TIME + BOOLEAN_TYPE +
            " )";

    MonitorsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
