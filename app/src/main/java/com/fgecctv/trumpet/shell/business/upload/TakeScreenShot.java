package com.fgecctv.trumpet.shell.business.upload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;

import com.fgecctv.ipc_client.IpcPresenter;
import com.fgecctv.trumpet.shell.app.AppEnvironment;

import java.io.File;

public class TakeScreenShot {

    private static String getBitmapPath() {
        String path = AppEnvironment.getScreenShotsPath();
        File apkPath = new File(path);
        if (!apkPath.getParentFile().exists())
            apkPath.getParentFile().mkdirs();
        if (!apkPath.exists())
            apkPath.mkdirs();
        return path;
    }

    public Bitmap takeScreenShotBitmap(IpcPresenter ipcPresenter){
        String path = getBitmapPath() + File.separator + System.currentTimeMillis()+".jpg";
        String cmdStr = "screencap -p " + path;
        ipcPresenter.sendMessage(cmdStr);
        return getBitmapFromFile(path);
    }

    private Bitmap getBitmapFromFile(String path){
        File file = new File(path);
        Bitmap bitmap ;
        while (!file.exists() || !file.canRead() || file.length() <= 0){
            SystemClock.sleep(500);
        }
        bitmap = BitmapFactory.decodeFile(path);
        return bitmap;
    }

}
