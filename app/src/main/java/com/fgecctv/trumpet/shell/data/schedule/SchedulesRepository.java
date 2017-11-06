package com.fgecctv.trumpet.shell.data.schedule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alibaba.fastjson.JSON;
import com.android.volley.toolbox.RequestFuture;
import com.fgecctv.trumpet.shell.data.ad.repository.SynchronizingException;
import com.fgecctv.trumpet.shell.data.schedule.SchedulesPersistenceContract.SchedulesEntry;
import com.fgecctv.trumpet.shell.network.http.AndroidHttpClient;
import com.fgecctv.trumpet.shell.network.http.EncryptedGetRequest;
import com.fgecctv.trumpet.shell.network.http.request.SyncRequestBody;
import com.fgecctv.trumpet.shell.network.http.response.RemoteResources;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SchedulesRepository {
    private static SchedulesRepository INSTANCE;
    private final String id;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final AndroidHttpClient httpClient;
    private SchedulesDbHelper dbHelper;

    private SchedulesRepository(Context context, String id) {
        dbHelper = new SchedulesDbHelper(context);
        this.httpClient = AndroidHttpClient.getInstance(context);
        this.id = id;
    }

    public static synchronized SchedulesRepository getInstance(Context context, String androidId) {
        if (INSTANCE == null)
            INSTANCE = new SchedulesRepository(context, androidId);
        return INSTANCE;
    }

    private void insertToSchedulesTable(List<ScheduleRecord> scheduleRecords) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (ScheduleRecord schedule : scheduleRecords) {
            ContentValues values = new ContentValues();
            values.put(SchedulesEntry.COLUMN_NAME_DAY_OF_WEEK, schedule.dayOfWeek);
            values.put(SchedulesEntry.COLUMN_NAME_CLOSE_HOUR, schedule.closeHour);
            values.put(SchedulesEntry.COLUMN_NAME_CLOSE_MINUTE, schedule.closeMinute);
            values.put(SchedulesEntry.COLUMN_NAME_POWER_HOUR, schedule.powerHour);
            values.put(SchedulesEntry.COLUMN_NAME_POWER_MINUTE, schedule.powerMinute);
            db.insert(SchedulesEntry.TABLE_NAME, null, values);
        }
        db.close();
    }

    private void deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(SchedulesPersistenceContract.SchedulesEntry.TABLE_NAME, null, null);
        db.close();
    }

    private List<ScheduleRecord> getScheduleRecords() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {SchedulesEntry.COLUMN_NAME_ID, SchedulesEntry.COLUMN_NAME_DAY_OF_WEEK, SchedulesEntry.COLUMN_NAME_POWER_HOUR, SchedulesEntry.COLUMN_NAME_POWER_MINUTE, SchedulesEntry.COLUMN_NAME_CLOSE_HOUR, SchedulesEntry.COLUMN_NAME_CLOSE_MINUTE};
        Cursor c = db.query(SchedulesEntry.TABLE_NAME, columns, null, null, null, null, null);
        List<ScheduleRecord> records = new ArrayList<>();
        while (c.moveToNext())
            records.add(new ScheduleRecord(c.getString(c.getColumnIndex(SchedulesEntry.COLUMN_NAME_DAY_OF_WEEK)), c.getString(c.getColumnIndex(SchedulesEntry.COLUMN_NAME_POWER_HOUR)), c.getString(c.getColumnIndex(SchedulesEntry.COLUMN_NAME_POWER_MINUTE)), c.getString(c.getColumnIndex(SchedulesEntry.COLUMN_NAME_CLOSE_HOUR)), c.getString(c.getColumnIndex(SchedulesEntry.COLUMN_NAME_CLOSE_MINUTE))));
        c.close();
        return records;
    }

    public void update(final SynchronizeSchedulesCallback callback) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                synchronize(callback);
            }
        });
    }

    private void synchronize(SynchronizeSchedulesCallback callback) {
        try {
            List<RemoteResources.TimeListBean> remoteSchedules = getRemoteSchedules();
            deleteAll();
            List<ScheduleRecord> localSchedule = new ArrayList<>();
            for (RemoteResources.TimeListBean m : remoteSchedules)
                localSchedule.add(new ScheduleRecord(m.week, m.powerHour, m.powerMinute, m.closeHour, m.closeMinute));
            insertToSchedulesTable(localSchedule);
            callback.onSchedulesSynchronized(localSchedule);
        } catch (SynchronizingException e) {
            List<ScheduleRecord> localSchedule = new ArrayList<>();
            for (ScheduleRecord m : getScheduleRecords())
                localSchedule.add(new ScheduleRecord(m.dayOfWeek, m.powerHour, m.powerMinute, m.closeHour, m.closeMinute));
            callback.onRemoteDataNotAvailable(localSchedule);
        }
    }

    private List<RemoteResources.TimeListBean> getRemoteSchedules() throws SynchronizingException {
        RequestFuture<String> future = RequestFuture.newFuture();
        SyncRequestBody request = new SyncRequestBody();
        request.terminalId = id;
        httpClient.sendRequest(new EncryptedGetRequest(request, future, future));
        String response;
        try {
            response = future.get();
        } catch (InterruptedException e) {
            throw new AssertionError("Fetching remote resources shouldn't be interrupted.");
        } catch (ExecutionException e) {
            throw new SynchronizingException("Network is unavailable currently.");
        }
        RemoteResources remoteResources = JSON.parseObject(response, RemoteResources.class);
        return remoteResources.timeList;
    }
}
