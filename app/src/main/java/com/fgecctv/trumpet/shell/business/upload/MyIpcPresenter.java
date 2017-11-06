package com.fgecctv.trumpet.shell.business.upload;

import android.content.Context;

import com.fgecctv.ipc_client.IpcPresenter;


public class MyIpcPresenter {
    private static IpcPresenter ipcPresenter = null;

    public static IpcPresenter getInstance(Context context){
        if (ipcPresenter == null) {
            synchronized (MyIpcPresenter.class){
                if (ipcPresenter == null)
                    ipcPresenter = new IpcPresenter(context);
                    ipcPresenter.start();
            }
        }
        return ipcPresenter;
    }
}
