package com.fgecctv.trumpet.shell.data.ad.repository;

import com.fgecctv.io.Files;
import com.fgecctv.trumpet.shell.app.AppEnvironment;
import com.fgecctv.trumpet.shell.network.http.AdDownloadListener;
import com.fgecctv.trumpet.shell.network.http.Extractor;
import com.fgecctv.trumpet.shell.network.http.ZipExtractor;
import com.google.common.base.Preconditions;

import java.io.File;
import java.io.IOException;

class AdFetcher {

    private Extractor extractor;

    private AdDownloader adDownloader;

    AdFetcher() {
        extractor = new ZipExtractor();
        adDownloader = new AdDownloader();
    }

    private void fetch(AdResource resource, AdDownloadListener listener) throws IOException {
        File zip;

        zip = adDownloader.download(resource, listener);

        Preconditions.checkState(zip.exists());

        extractor.extract(zip, makeAdDirectory(resource.getId()));

        Files.delete(zip);
    }

    void fetch(AdResource resource, AdDownloadListener listener, boolean overwrite) throws IOException {
        if (!getAdDirectory(resource.getId()).exists()) {
            fetch(resource, listener);
            return;
        }

        if (overwrite) {
            Files.delete(getAdDirectory(resource.getId()));
            fetch(resource, listener);
        }
    }

    private File getAdDirectory(String id) {
        return new File(AppEnvironment.getExternalStoragePath() +
                File.separator + "Ads" +
                File.separator + id);
    }

    private File makeAdDirectory(String id) {
        return Files.mkdirs(AppEnvironment.getExternalStoragePath() +
                File.separator + "Ads" +
                File.separator + id);
    }
}