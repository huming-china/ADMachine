package com.fgecctv.trumpet.shell.data.ad.repository;

import com.fgecctv.trumpet.shell.app.AppEnvironment;
import com.fgecctv.trumpet.shell.network.http.AdDownloadListener;
import com.google.common.io.ByteProcessor;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.squirrel.voyage.Voyage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

class AdDownloader {

    private static final String TAG = "AdDownloader";

    File download(final AdResource resource, final AdDownloadListener listener) throws IOException {

        final File zip = new File(getDownloadsPath() + File.separator + resource.getId());

        if (zip.exists() && !zip.delete()) {
            String cause = "Unable to delete " + zip;
            Voyage.e(TAG, cause);
            throw new IOException(cause);
        }

        Files.createParentDirs(zip);

        URL url = new URL(resource.getUrl());
        ByteSource byteSource = Resources.asByteSource(url);
        final long totalSize = byteSource.size();
        final ByteSink byteSink = Files.asByteSink(zip, FileWriteMode.APPEND);

        return byteSource.read(new ByteProcessor<File>() {
            long completedSize;

            @Override
            public boolean processBytes(byte[] buf, int off, int len) throws IOException {
                byte[] buffer = Arrays.copyOfRange(buf, off, len);
                byteSink.write(buffer);

                completedSize += len;
                listener.onProgress(resource.getId(), (float) completedSize / totalSize);
                return true;
            }

            @Override
            public File getResult() {
                return zip;
            }
        });
    }

    private String getDownloadsPath() {
        return AppEnvironment.getExternalStoragePath() + File.separator + "Downloads";
    }
}
