package com.fgecctv.trumpet.shell.data.schedule;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fgecctv.trumpet.shell.data.schedule.SchedulesPersistenceContract.SchedulesEntry;

class SchedulesDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "Schedules.db";

    private static final String TEXT_TYPE = " TEXT";

    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SchedulesEntry.TABLE_NAME + " (" +
                    SchedulesEntry.COLUMN_NAME_ID + TEXT_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    SchedulesEntry.COLUMN_NAME_DAY_OF_WEEK + TEXT_TYPE + COMMA_SEP +
                    SchedulesEntry.COLUMN_NAME_CLOSE_HOUR + TEXT_TYPE + COMMA_SEP +
                    SchedulesEntry.COLUMN_NAME_CLOSE_MINUTE + TEXT_TYPE + COMMA_SEP +
                    SchedulesEntry.COLUMN_NAME_POWER_HOUR + TEXT_TYPE + COMMA_SEP +
                    SchedulesEntry.COLUMN_NAME_POWER_MINUTE + TEXT_TYPE +
                    " )";

     SchedulesDbHelper(Context context) {
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
