package com.fgecctv.trumpet.shell.business.advertisement;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.fgecctv.trumpet.shell.business.BasePresenter;
import com.fgecctv.trumpet.shell.business.BaseView;
import com.fgecctv.view.MarqueeView;

import java.util.List;

interface AdvertisementContract {

    interface View extends BaseView<Presenter> {

        void showBulletin(MarqueeView.Params params);

        void cancelBulletin();

        String getString(int resId);

        void setVolume(int volume);

        void play(String uri);

        void startMonitorPlayerAsync(@NonNull List<String> urls, int duration);

        void setMonitorWindowLocation(Rect rect);

        void showMessage(String status);

        void synchronizing();

        void registerReceiver(BroadcastReceiver receiver, IntentFilter filter);

        void unregisterReceiver(BroadcastReceiver receiver);

        Bitmap takeScreenshot();

        void restart();

    }

    interface Presenter extends BasePresenter {
        void onNetworkActiveListener();
    }
}
