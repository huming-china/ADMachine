package com.fgecctv.trumpet.shell.network.http;

public interface AdDownloadListener {

    void onProgress(String adId, float percentage);
}