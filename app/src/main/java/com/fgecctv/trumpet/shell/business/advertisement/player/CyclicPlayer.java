package com.fgecctv.trumpet.shell.business.advertisement.player;

import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A player to circularly play all the {@link Playable} automatically.
 */
public class CyclicPlayer {

    static final int MSG_WHAT_RESTART = 0;
    static final int MSG_WHAT_ON_START = 2;
    static final int MSG_WHAT_ON_STOP = 3;

    final View view;

    private List<Playable> playlist = new LinkedList<>();

    Handler cyclicHandler;
    Handler adHandler;

    public CyclicPlayer(View view) {
        this.view = view;
        this.cyclicHandler = new Handler(new Callback(this));
        this.adHandler = new Handler(new Callback(this));
    }

    /**
     * Add a new {@link Playable} to the playlist of this {@link CyclicPlayer}. The
     * {@link CyclicPlayer} it self will circularly play all the {@link Playable} in the playlist as
     * long as the playlist is not empty.
     * <p>
     * Adding one {@link Playable} twice is acceptable. The {@link CyclicPlayer} will play
     * it twice in one loop.
     *
     * @param p the {@link Playable} you want to add in the playlist.
     */
    public void add(@NonNull Playable p) {
        playlist.add(p);
    }

    /**
     * Remove a specific {@link Playable} according to its id.
     * <p>
     * If there are more than one {@link Playable} in the playlist, invoking this method once will
     * randomly remove only one {@link Playable}.
     *
     * @param id the id of corresponding {@link Playable} you want to remove from the playlist.
     */
    private void remove(@Nullable String id) {
        cyclicHandler.removeCallbacksAndMessages(null);
        Iterator<Playable> iterator = playlist.iterator();
        while (iterator.hasNext()) {
            Playable playable = iterator.next();
            if (playable.getId().equals(id)) {
                iterator.remove();
                break;
            }
        }
    }

    public void clear() {
        playlist.clear();
        cyclicHandler.removeCallbacksAndMessages(null);
        adHandler.removeCallbacksAndMessages(null);
    }

    public void invalidate() {
        cyclicHandler.removeCallbacks(null);
        Message.obtain(cyclicHandler, MSG_WHAT_RESTART, playlist).sendToTarget();
    }

    public void setPlaylist(List<TimedAdTask> playlist) {
        long currentTime = System.currentTimeMillis();
        for (TimedAdTask task : playlist) {
            if (task.getEndTime() < currentTime)
                continue;

            Message startMessage = Message.obtain(adHandler, MSG_WHAT_ON_START, task);
            adHandler.sendMessageDelayed(startMessage, task.getStartTime() - currentTime);
            Message StopMessage = Message.obtain(adHandler, MSG_WHAT_ON_STOP, task);
            adHandler.sendMessageDelayed(StopMessage, task.getEndTime() - currentTime);
        }
    }

    void onStart(AdTask task) {
        cyclicHandler.removeCallbacksAndMessages(null);
        add(task);
        invalidate();
    }

    void onStop(AdTask task) {
        remove(task.getId());
        invalidate();
    }

    /**
     * The view layer of {@link CyclicPlayer}. It consists of two parts: the uri of an ad and the
     * position of the window of a monitor.
     */
    public interface View {

        /**
         * Present a uri, usually with {@link android.webkit.WebView}, and specify the frame of
         * where to locate a monitor.
         *
         * @param uri   The uri need to be played.
         * @param frame The location of the monitor window.
         */
        void play(Uri uri, Rect frame);

        /**
         * Show blank page.
         */
        void stop();
    }
}
