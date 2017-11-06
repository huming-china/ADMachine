package com.fgecctv.trumpet.shell.data.ad.repository;

import android.support.annotation.NonNull;

import java.util.List;

interface AdsDataSource {

    void insert(@NonNull AdRecord adRecord);

    void delete(@NonNull String id);

    List<AdRecord> query();

    AdRecord query(@NonNull String id);
}