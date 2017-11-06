package com.fgecctv.trumpet.shell.business.advertisement.player;

import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static com.fgecctv.trumpet.shell.business.advertisement.player.CyclicPlayer.MSG_WHAT_ON_START;
import static com.fgecctv.trumpet.shell.business.advertisement.player.CyclicPlayer.MSG_WHAT_ON_STOP;
import static com.fgecctv.trumpet.shell.business.advertisement.player.CyclicPlayer.MSG_WHAT_RESTART;

class Callback implements Handler.Callback {

    private static final String TAG = "Callback";

    private static final int MSG_WHAT_START = 1;

    private WeakReference<CyclicPlayer> playerWeakReference;

    Callback(CyclicPlayer player) {
        playerWeakReference = new WeakReference<>(player);
    }

    private ListIterator<Playable> iterator;

    @Override
    public boolean handleMessage(Message msg) {
        CyclicPlayer player = playerWeakReference.get();
        if (player == null)
            return true;

        switch (msg.what) {
            case MSG_WHAT_RESTART:
                List<Playable> playlist = new ArrayList<>((List<Playable>) msg.obj);
                iterator = playlist.listIterator();

                if (playlist.isEmpty()) {
                    player.view.play(Uri.parse("file:///android_asset/blank.html"), new Rect());
                    break;
                }

                Playable exclusivePlayable = retrieveExclusivePlayable();

                if (exclusivePlayable != null)
                    trimNotExclusivePlayable();

            case MSG_WHAT_START:
                if (!iterator.hasNext())
                    resetIterator();

                Playable playable = iterator.next();
                player.view.play(playable.getUri(), playable.getFrameOfMonitorWindow());
                player.cyclicHandler.sendEmptyMessageDelayed(MSG_WHAT_START, playable.getDuration());
                break;
            case MSG_WHAT_ON_START:
                player.onStart((AdTask) msg.obj);
                break;
            case MSG_WHAT_ON_STOP:
                player.onStop((AdTask) msg.obj);
                break;
            default:
                return false;
        }
        return true;
    }

    private void trimNotExclusivePlayable() {
        resetIterator();
        while (iterator.hasNext()) {
            if (!iterator.next().isExclusive())
                iterator.remove();
        }
    }

    @Nullable
    private Playable retrieveExclusivePlayable() {
        while (iterator.hasNext()) {
            Playable next = iterator.next();
            if (next.isExclusive())
                return next;
        }

        return null;
    }

    private void resetIterator() {
        while (iterator.hasPrevious()) iterator.previous();
    }
}