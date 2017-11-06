package com.fgecctv.trumpet.shell.business.advertisement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.fgecctv.hardware.Power;
import com.fgecctv.io.Files;
import com.fgecctv.ipc_client.IpcPresenter;
import com.fgecctv.trumpet.shell.R;
import com.fgecctv.trumpet.shell.app.AppEnvironment;
import com.fgecctv.trumpet.shell.business.upload.MyIpcPresenter;
import com.fgecctv.trumpet.shell.business.upload.TakeScreenShot;
import com.fgecctv.trumpet.shell.data.ad.repository.Ad;
import com.fgecctv.view.MarqueeView;
import com.squirrel.media.MonitorPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AdvertisementFragment extends Fragment implements AdvertisementContract.View {
    @SuppressWarnings("unused")
    private static final String TAG = "AdvertisementFragment";
    @Bind(R.id.marqueeView)
    MarqueeView bulletin;
    @Bind(R.id.webView)
    WebView webView;
    @Bind(R.id.surfaceView)
    SurfaceView surfaceView;
    private AdvertisementContract.Presenter presenter;
    private TakeScreenShot screenShot;
    private IpcPresenter ipcPresenter;

    private List<String> urls = new ArrayList<>();
    private long duration;

    private MonitorPlayer monitorPlayer;
    private SurfaceHolder holder;

    private MonitorPlayerState monitorPlayerState = new MonitorPlayerState();
    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(final SurfaceHolder holder) {
            monitorPlayerState.setSurfaceValid(true);
            if (monitorPlayerState.isReady())
                startMonitorPlayer();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            monitorPlayerState.setSurfaceValid(false);

            if (monitorPlayer != null)
                stopMonitorPlayer();
        }
    };

    private BroadcastReceiver receiver;

    @NonNull
    public static AdvertisementFragment newInstance() {
        return new AdvertisementFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adversiting, container, false);
        ButterKnife.bind(this, view);

        ipcPresenter = MyIpcPresenter.getInstance(getContext());

        Ad blank = new Ad.Builder().create();

        webView.loadUrl(blank.getUri());
        webView.setKeepScreenOn(true);
        webView.setWebViewClient(new AutoPlayWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        screenShot = new TakeScreenShot();

        surfaceView.setZOrderOnTop(true);
        holder = surfaceView.getHolder();
        holder.addCallback(callback);

        receiver = new NetworkChangedReceiver();

        presenter.start();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getContext().registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();

        getContext().unregisterReceiver(receiver);
    }

    private void startMonitorPlayer() {
        if (monitorPlayer != null)
            monitorPlayer.stop();

        if (!urls.isEmpty()) {
            monitorPlayer = new MonitorPlayer(getActivity(), holder);
            monitorPlayer.start(urls, duration);
        }
    }

    private void stopMonitorPlayer() {
        monitorPlayer.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.stop();
        webView.destroy();
    }

    @Override
    public void showBulletin(MarqueeView.Params params) {
        bulletin.setText(params.text);
        bulletin.resetOffset();
        bulletin.setVisibility(View.VISIBLE);
    }

    @Override
    public void cancelBulletin() {
        bulletin.setVisibility(View.GONE);
    }

    @Override
    public void setVolume(int volume) {
        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        int i = (int) (volume / 100.0 * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, AudioManager.FLAG_PLAY_SOUND);
    }

    @Override
    public void play(String url) {
        webView.loadUrl(url);
    }

    @Override
    public void startMonitorPlayerAsync(@NonNull final List<String> urls, final int duration) {
        this.urls = urls;
        this.duration = TimeUnit.SECONDS.toMillis(duration);

        monitorPlayerState.setMonitorConfigured(true);

        if (monitorPlayerState.isReady())
            startMonitorPlayer();
    }

    @Override
    public void setMonitorWindowLocation(Rect rect) {
        if (rect.width() == 0 && rect.height() == 0)
            surfaceView.setVisibility(View.GONE);
        else
            surfaceView.setVisibility(View.VISIBLE);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) surfaceView.getLayoutParams();
        layoutParams.leftMargin = rect.left;
        layoutParams.topMargin = rect.top;
        layoutParams.width = rect.width();
        layoutParams.height = rect.height();
        surfaceView.setLayoutParams(layoutParams);
    }

    @Override
    public void showMessage(String status) {
        Toast.makeText(getActivity(), status, Toast.LENGTH_LONG).show();
    }

    @Override
    public void synchronizing() {
        setMonitorWindowLocation(new Rect());
        Files.delete(AppEnvironment.getScreenShotsPath());
        webView.loadUrl("file:///android_asset/synchronizing.html");
    }

    @Override
    public void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        getContext().registerReceiver(receiver, filter);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        getContext().unregisterReceiver(receiver);
    }

    @Override
    public Bitmap takeScreenshot() {
        Bitmap bitmap = screenShot.takeScreenShotBitmap(ipcPresenter);
        if (bitmap == null)
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.screen_fail);
        return bitmap;
    }

    @Override
    public void setPresenter(AdvertisementContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void restart() {
        Power.restart(ipcPresenter);
    }

    private class NetworkChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo networkInfo = intent.getParcelableExtra("networkInfo");
            if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                presenter.onNetworkActiveListener();
            }
        }
    }
}