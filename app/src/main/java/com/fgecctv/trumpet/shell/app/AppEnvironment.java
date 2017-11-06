package com.fgecctv.trumpet.shell.app;

import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;

public class AppEnvironment {

    @NonNull
    public static String getExternalStoragePath() {
        return Environment.getExternalStorageDirectory() +
                File.separator + "ADMachine";
    }

    @NonNull
    public static String getScreenShotsPath() {
        return Environment.getExternalStorageDirectory() +
                File.separator + "ADMachine"+
                File.separator + "screenShots";
    }
}
