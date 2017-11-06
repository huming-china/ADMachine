package com.fgecctv.trumpet.shell.business.advertisement;

import android.graphics.Rect;
import android.net.Uri;

import com.fgecctv.trumpet.shell.business.advertisement.player.CyclicPlayer.View;
import com.fgecctv.trumpet.shell.data.ad.repository.Ad;

class WebPlayerViewAdapter implements View {

    private final AdvertisementContract.View view;

    WebPlayerViewAdapter(AdvertisementContract.View view) {
        this.view = view;
    }

    @Override
    public void play(Uri uri, Rect windowLocation) {
        view.play(uri.toString());
        view.setMonitorWindowLocation(windowLocation);
    }

    @Override
    public void stop() {
        Ad blank = new Ad.Builder().create();
        view.play(blank.getUri());
        view.setMonitorWindowLocation(new Rect());
    }
}
