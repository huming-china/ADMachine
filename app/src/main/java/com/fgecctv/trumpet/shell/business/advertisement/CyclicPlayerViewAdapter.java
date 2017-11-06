package com.fgecctv.trumpet.shell.business.advertisement;

import android.graphics.Rect;
import android.net.Uri;

import com.fgecctv.trumpet.shell.business.advertisement.player.CyclicPlayer;

class CyclicPlayerViewAdapter implements CyclicPlayer.View {
    private AdvertisementContract.View view;

    CyclicPlayerViewAdapter(AdvertisementContract.View view) {
        this.view = view;
    }

    @Override
    public void play(Uri uri, Rect frame) {
        view.play(uri.toString());
        view.setMonitorWindowLocation(frame);
    }

    @Override
    public void stop() {
        view.play("file:///android_asset/blank.html");
        view.setMonitorWindowLocation(new Rect());
    }
}
