package com.fgecctv.trumpet.shell.business.update;

import android.content.Context;
import android.util.Log;

import com.fgecctv.ipc_client.IpcPresenter;
import com.fgecctv.trumpet.shell.business.upload.MyIpcPresenter;

import org.json.JSONArray;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


public class SelfUpdate {
    private static final long DELAY_TIME = 1000 * 60 * 30;
    private static final long PERIOD_TIME = 1000 * 60 * 60 * 24;
    private boolean hasNewestVersion;
    private CheckVersionUtils checkVersionUtils = new CheckVersionUtils();
    private Timer timer;
    private IpcPresenter ipcPresenter;

    public void checkUpdate(final Context context){
        ipcPresenter = MyIpcPresenter.getInstance(context);
        timer = new Timer("check for new version");
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                hasNewestVersion = checkForNewVersion();

                if (hasNewestVersion) {
                    String apkUrl = checkVersionUtils.getVersionUpdateBean().url;
                    installAndOpenNewVersionApp(apkUrl);
                    hasNewestVersion = false;
                }
            }
        };
        timer.schedule(task, DELAY_TIME, PERIOD_TIME);
    }

    private boolean checkForNewVersion() {
        return checkVersionUtils.hasNewVersion();
    }

    private void installAndOpenNewVersionApp(String apkUrl) {
        File file = DownLoader.downloadAndroidPackage(apkUrl);
        if (file == null) {
            Log.e("SelfUpdate", "run: file is null");
            return;
        }
        String path = file.getAbsolutePath();
        JSONArray cmdArray = new JSONArray();
        cmdArray.put("pm install -r " + path);
        cmdArray.put("am start -n com.fgecctv.trumpet.shell/.business.bind.BindActivity");
        String cmdStr = cmdArray.toString();
        Log.e("SelfUpdate", "run: " + cmdStr);
        ipcPresenter.sendMessage(cmdStr);
    }

    public void onDestroy(){
        if (timer != null) timer.cancel();
    }

}
