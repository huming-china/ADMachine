package com.fgecctv.trumpet.shell.data.ad.repository;

class AdsPersistenceContract {
         static abstract class AdsEntry {
         static final String TABLE_NAME = "ads";
         static final String COLUMN_NAME_ID = "_id";
         static final String COLUMN_NAME_URI = "_uri";
         static final String COLUMN_NAME_DURATION = "_duration";
         static final String COLUMN_NAME_RECT_LEFT = "_left";
         static final String COLUMN_NAME_RECT_TOP = "_top";
         static final String COLUMN_NAME_RECT_WIDTH = "_width";
         static final String COLUMN_NAME_RECT_HEIGHT = "_height";
         static final String COLUMN_NAME_SCHEDULE = "_schedule";
         static final String COLUMN_NAME_PRIORITY = "_priority";
         static final String COLUMN_NAME_EFFECTIVE_DATE = "_effectiveDate";
         static final String COLUMN_NAME_EXPIRY_DATE = "_expiryDate";
         static final String COLUMN_NAME_TIMESTAMP = "_timestamp";
    }
}
