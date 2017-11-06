package com.fgecctv.trumpet.shell.data.ad.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fgecctv.trumpet.shell.data.ad.Repeat;

import java.util.ArrayList;
import java.util.List;

import static com.fgecctv.trumpet.shell.data.ad.repository.AdsPersistenceContract.AdsEntry;

class AdsDataBase implements AdsDataSource {

    private AdsDbHelper dbHelper;

    AdsDataBase(Context context) {
        dbHelper = new AdsDbHelper(context);
    }

    @Override
    public void insert(@NonNull AdRecord adRecord) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String jsonString = JSONArray.toJSONString(adRecord.getRepeats());

        ContentValues values = new ContentValues();
        values.put(AdsEntry.COLUMN_NAME_ID, adRecord.getId());
        values.put(AdsEntry.COLUMN_NAME_URI, adRecord.getUri());
        values.put(AdsEntry.COLUMN_NAME_DURATION, adRecord.getDuration());
        values.put(AdsEntry.COLUMN_NAME_RECT_LEFT, adRecord.getMonitorWindowLocation().left);
        values.put(AdsEntry.COLUMN_NAME_RECT_TOP, adRecord.getMonitorWindowLocation().top);
        values.put(AdsEntry.COLUMN_NAME_RECT_WIDTH, adRecord.getMonitorWindowLocation().width());
        values.put(AdsEntry.COLUMN_NAME_RECT_HEIGHT, adRecord.getMonitorWindowLocation().height());
        values.put(AdsEntry.COLUMN_NAME_SCHEDULE, jsonString);
        values.put(AdsEntry.COLUMN_NAME_PRIORITY, adRecord.getPriority());
        values.put(AdsEntry.COLUMN_NAME_EXPIRY_DATE, adRecord.getExpiryDate());
        values.put(AdsEntry.COLUMN_NAME_EFFECTIVE_DATE, adRecord.getEffectiveDate());
        values.put(AdsEntry.COLUMN_NAME_TIMESTAMP, adRecord.getTimestamp());

        db.insert(AdsEntry.TABLE_NAME, null, values);

        db.close();
    }

    @Override
    public void delete(@NonNull String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = AdsEntry.COLUMN_NAME_ID + " LIKE ?";
        String[] selectionArgs = {id};
        db.delete(AdsEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }

    @Override
    public List<AdRecord> query() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = new String[]{
                AdsEntry.COLUMN_NAME_ID,
                AdsEntry.COLUMN_NAME_URI,
                AdsEntry.COLUMN_NAME_DURATION,
                AdsEntry.COLUMN_NAME_RECT_LEFT,
                AdsEntry.COLUMN_NAME_RECT_TOP,
                AdsEntry.COLUMN_NAME_RECT_WIDTH,
                AdsEntry.COLUMN_NAME_RECT_HEIGHT,
                AdsEntry.COLUMN_NAME_SCHEDULE,
                AdsEntry.COLUMN_NAME_PRIORITY,
                AdsEntry.COLUMN_NAME_EXPIRY_DATE,
                AdsEntry.COLUMN_NAME_EFFECTIVE_DATE,
                AdsEntry.COLUMN_NAME_TIMESTAMP
        };

        Cursor c = db.query(AdsEntry.TABLE_NAME, columns, null, null, null, null, null);

        List<AdRecord> adRecords = new ArrayList<>(c.getCount());

        while (c.moveToNext()) {
            String jsonString = c.getString(c.getColumnIndex(AdsEntry.COLUMN_NAME_SCHEDULE));
            List<Repeat> repeats = JSON.parseArray(jsonString, Repeat.class);
            int l = c.getInt(c.getColumnIndex(AdsEntry.COLUMN_NAME_RECT_LEFT));
            int t = c.getInt(c.getColumnIndex(AdsEntry.COLUMN_NAME_RECT_TOP));
            int w = c.getInt(c.getColumnIndex(AdsEntry.COLUMN_NAME_RECT_WIDTH));
            int h = c.getInt(c.getColumnIndex(AdsEntry.COLUMN_NAME_RECT_HEIGHT));
            Rect rect = new Rect(l, t, w + l, h + t);

            adRecords.add(new AdRecord.Builder()
                    .setId(c.getString(c.getColumnIndex(AdsEntry.COLUMN_NAME_ID)))
                    .setUri(c.getString(c.getColumnIndex(AdsEntry.COLUMN_NAME_URI)))
                    .setDuration(c.getLong(c.getColumnIndex(AdsEntry.COLUMN_NAME_DURATION)))
                    .setPriority(c.getLong(c.getColumnIndex(AdsEntry.COLUMN_NAME_PRIORITY)))
                    .setEffectiveDate(c.getLong(c.getColumnIndex(AdsEntry.COLUMN_NAME_EFFECTIVE_DATE)))
                    .setExpiryDate(c.getLong(c.getColumnIndex(AdsEntry.COLUMN_NAME_EXPIRY_DATE)))
                    .setTimestamp(c.getLong(c.getColumnIndex(AdsEntry.COLUMN_NAME_TIMESTAMP)))
                    .setRepeats(repeats)
                    .setMonitorWindowLocation(rect)
                    .create());
        }

        c.close();

        return adRecords;
    }

    @Override
    public AdRecord query(@NonNull String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = new String[]{
                AdsEntry.COLUMN_NAME_ID,
                AdsEntry.COLUMN_NAME_URI,
                AdsEntry.COLUMN_NAME_DURATION,
                AdsEntry.COLUMN_NAME_RECT_LEFT,
                AdsEntry.COLUMN_NAME_RECT_TOP,
                AdsEntry.COLUMN_NAME_RECT_WIDTH,
                AdsEntry.COLUMN_NAME_RECT_HEIGHT,
                AdsEntry.COLUMN_NAME_SCHEDULE,
                AdsEntry.COLUMN_NAME_PRIORITY,
                AdsEntry.COLUMN_NAME_EXPIRY_DATE,
                AdsEntry.COLUMN_NAME_EFFECTIVE_DATE,
                AdsEntry.COLUMN_NAME_TIMESTAMP
        };

        String selection = AdsEntry.COLUMN_NAME_ID + " LIKE ?";
        String[] selectionArgs = {id};

        Cursor c = db.query(AdsEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, null);

        AdRecord adRecord = new AdRecord.Builder().create();

        if (c.moveToNext()) {
            String jsonString = c.getString(c.getColumnIndex(AdsEntry.COLUMN_NAME_SCHEDULE));
            List<Repeat> repeats = JSON.parseArray(jsonString, Repeat.class);
            int left = c.getInt(c.getColumnIndex(AdsEntry.COLUMN_NAME_RECT_LEFT));
            int top = c.getInt(c.getColumnIndex(AdsEntry.COLUMN_NAME_RECT_TOP));
            int width = c.getInt(c.getColumnIndex(AdsEntry.COLUMN_NAME_RECT_WIDTH));
            int height = c.getInt(c.getColumnIndex(AdsEntry.COLUMN_NAME_RECT_HEIGHT));
            Rect rect = new Rect(left, top, width + left, height + top);

            adRecord = new AdRecord.Builder()
                    .setId(c.getString(c.getColumnIndex(AdsEntry.COLUMN_NAME_ID)))
                    .setUri(c.getString(c.getColumnIndex(AdsEntry.COLUMN_NAME_URI)))
                    .setDuration(c.getLong(c.getColumnIndex(AdsEntry.COLUMN_NAME_DURATION)))
                    .setPriority(c.getLong(c.getColumnIndex(AdsEntry.COLUMN_NAME_PRIORITY)))
                    .setEffectiveDate(c.getLong(c.getColumnIndex(AdsEntry.COLUMN_NAME_EFFECTIVE_DATE)))
                    .setExpiryDate(c.getLong(c.getColumnIndex(AdsEntry.COLUMN_NAME_EXPIRY_DATE)))
                    .setTimestamp(c.getLong(c.getColumnIndex(AdsEntry.COLUMN_NAME_TIMESTAMP)))
                    .setRepeats(repeats)
                    .setMonitorWindowLocation(rect)
                    .create();
        }

        c.close();

        return adRecord;
    }
}
