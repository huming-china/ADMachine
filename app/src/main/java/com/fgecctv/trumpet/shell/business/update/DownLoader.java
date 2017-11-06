package com.fgecctv.trumpet.shell.business.update;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class DownLoader {
     private static String getFileName(URL url) {
        String[] split = url.getPath().split("/");
        String path = Environment.getExternalStorageDirectory() +
                File.separator + "ADMachine" +
                File.separator + "apkDownload" +
                File.separator + split[split.length - 1];
        File apkPath = new File(path);
        if (!apkPath.getParentFile().exists())
            apkPath.getParentFile().mkdirs();
        return path;
    }


     static File downloadAndroidPackage(String urlStr) {
        InputStream inputStream = null;
        FileOutputStream fos = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = conn.getInputStream();
                byte[] bytes = new byte[4 * 1024];
                int read;
                File targetFile = new File(getFileName(url));
                if (!targetFile.exists()) {
                    fos = new FileOutputStream(targetFile);
                    while ((read = inputStream.read(bytes)) != -1) {
                        fos.write(bytes, 0, read);
                    }
                    fos.flush();
                }
                return targetFile;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}



