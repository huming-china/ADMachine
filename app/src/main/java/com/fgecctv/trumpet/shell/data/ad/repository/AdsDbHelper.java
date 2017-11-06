package com.fgecctv.trumpet.shell.data.ad.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.fgecctv.trumpet.shell.data.ad.repository.AdsPersistenceContract.AdsEntry;

class AdsDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "Ads.db";

    private static final String TEXT_TYPE = " TEXT";

    private static final String COMMA_SEP = ",";

    /**
     * 开机同步文件系统的时候，删除不在数据库里的广告目录
     */
    private final String SQL_CREATE_PLAYLIST_ENTRIES = "CREATE TABLE " + AdsEntry.TABLE_NAME + " (" +
            AdsEntry.COLUMN_NAME_ID + TEXT_TYPE + " PRIMARY KEY" + COMMA_SEP +
            AdsEntry.COLUMN_NAME_URI + TEXT_TYPE + COMMA_SEP +
            AdsEntry.COLUMN_NAME_TIMESTAMP + TEXT_TYPE + COMMA_SEP +
            AdsEntry.COLUMN_NAME_DURATION + TEXT_TYPE + COMMA_SEP +
            AdsEntry.COLUMN_NAME_RECT_LEFT + TEXT_TYPE + COMMA_SEP +
            AdsEntry.COLUMN_NAME_RECT_TOP + TEXT_TYPE + COMMA_SEP +
            AdsEntry.COLUMN_NAME_RECT_WIDTH + TEXT_TYPE + COMMA_SEP +
            AdsEntry.COLUMN_NAME_RECT_HEIGHT + TEXT_TYPE + COMMA_SEP +
            AdsEntry.COLUMN_NAME_SCHEDULE + TEXT_TYPE + COMMA_SEP +
            AdsEntry.COLUMN_NAME_PRIORITY + TEXT_TYPE + COMMA_SEP +
            AdsEntry.COLUMN_NAME_EFFECTIVE_DATE + TEXT_TYPE + COMMA_SEP +
            AdsEntry.COLUMN_NAME_EXPIRY_DATE + TEXT_TYPE +
            " )";

    public AdsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PLAYLIST_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}