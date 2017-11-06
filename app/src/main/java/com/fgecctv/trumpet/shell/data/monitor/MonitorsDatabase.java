package com.fgecctv.trumpet.shell.data.monitor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.fgecctv.trumpet.shell.data.monitor.MonitorsPersistenceContract.MonitorEntry;

class MonitorsDatabase implements MonitorDataSource {

    private MonitorsDbHelper dbHelper;

    MonitorsDatabase(Context context) {
        dbHelper = new MonitorsDbHelper(context);
    }

    @Override
    public void insert(List<MonitorRecord> records) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (MonitorRecord record : records) {
            ContentValues values = new ContentValues();

            values.put(MonitorEntry.COLUMN_NAME_IP, record.ip);
            values.put(MonitorEntry.COLUMN_NAME_GROUP, record.group);
            values.put(MonitorEntry.COLUMN_NAME_DELAY_TIME, record.delayTime);
            values.put(MonitorEntry.COLUMN_NAME_TRANSIT_TIME, record.transitTime);

            db.insert(MonitorEntry.TABLE_NAME, null, values);
        }

        db.close();
    }

    @Override
    public List<MonitorRecord> query() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = new String[]{
                MonitorEntry.COLUMN_NAME_IP,
                MonitorEntry.COLUMN_NAME_GROUP,
                MonitorEntry.COLUMN_NAME_DELAY_TIME,
                MonitorEntry.COLUMN_NAME_TRANSIT_TIME
        };

        Cursor cursor = db.query(MonitorEntry.TABLE_NAME, columns, null, null, null, null, null);
        List<MonitorRecord> records = new ArrayList<>(cursor.getCount());
        while (cursor.moveToNext())
            records.add(new MonitorRecord.Builder()
                    .setIp(cursor.getString(cursor.getColumnIndexOrThrow(MonitorEntry.COLUMN_NAME_IP)))
                    .setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MonitorEntry.COLUMN_NAME_TRANSIT_TIME)))
                    .create());

        cursor.close();
        db.close();
        return records;
    }

    @Override
    public void deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(MonitorEntry.TABLE_NAME, null, null);
        db.close();
    }
}
