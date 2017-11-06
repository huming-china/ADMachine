package com.fgecctv.trumpet.shell.data.ad.repository;

import java.util.List;

public interface SynchronizeAdsCallback {

    void onPreAdsSynchronize();

    void onAdsSynchronized(List<Ad> ads);

    void onRemoteDataNotAvailable(List<Ad> ads);
}
