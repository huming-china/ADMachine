package com.fgecctv.trumpet.shell.business.advertisement;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fgecctv.hardware.Power;
import com.fgecctv.ipc_client.IpcPresenter;
import com.fgecctv.trumpet.shell.BuildConfig;
import com.fgecctv.trumpet.shell.R;
import com.fgecctv.trumpet.shell.business.advertisement.player.AdTaskExecutor;
import com.fgecctv.trumpet.shell.business.advertisement.player.CyclicPlayer;
import com.fgecctv.trumpet.shell.business.upload.MyIpcPresenter;
import com.fgecctv.trumpet.shell.business.upload.OnUpLoadInfoCallBack;
import com.fgecctv.trumpet.shell.business.upload.SharedUtils;
import com.fgecctv.trumpet.shell.data.ad.repository.Ad;
import com.fgecctv.trumpet.shell.data.ad.repository.AdsRepository;
import com.fgecctv.trumpet.shell.data.ad.repository.SynchronizeAdsCallback;
import com.fgecctv.trumpet.shell.data.date.RemountDate;
import com.fgecctv.trumpet.shell.data.date.SyncDateCallBack;
import com.fgecctv.trumpet.shell.data.monitor.Monitors;
import com.fgecctv.trumpet.shell.data.monitor.MonitorsRepository;
import com.fgecctv.trumpet.shell.data.monitor.SynchronizeMonitorsCallback;
import com.fgecctv.trumpet.shell.data.schedule.ScheduleRecord;
import com.fgecctv.trumpet.shell.data.schedule.SchedulesRepository;
import com.fgecctv.trumpet.shell.data.schedule.SynchronizeSchedulesCallback;
import com.fgecctv.trumpet.shell.network.http.response.TimeResponse;
import com.fgecctv.trumpet.shell.network.mqtt.AndroidMqttClient;
import com.fgecctv.trumpet.shell.network.mqtt.ConnectivityChangedReceiver;
import com.fgecctv.trumpet.shell.network.mqtt.command.Advertise;
import com.fgecctv.trumpet.shell.network.mqtt.command.CancelAd;
import com.fgecctv.trumpet.shell.network.mqtt.command.CancelHeadline;
import com.fgecctv.trumpet.shell.network.mqtt.command.MonitorsCommand;
import com.fgecctv.trumpet.shell.network.mqtt.command.PublishNews;
import com.fgecctv.trumpet.shell.network.mqtt.command.RestartCommand;
import com.fgecctv.trumpet.shell.network.mqtt.command.ScheduleSettings;
import com.fgecctv.trumpet.shell.network.mqtt.command.SetVolumeCommand;
import com.squirrel.voyage.Voyage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

class AdvertisementPresenter implements AdvertisementContract.Presenter {

    private static final int MSG_TYPE_SHOW_MESSAGE = 1;
    private static final int MSG_TYPE_SET_MONITOR = 2;
    private static final int MSG_TYPE_SET_SCHEDULE = 3;
    private static final int MSG_TYPE_SHOW_SYNCHRONIZING_STATUS = 4;
    private static final String SHARED_NAME = "Advertisement";
    private final MonitorsRepository monitorsRepository;
    private final AdsRepository adsRepository;
    private final SchedulesRepository schedulesRepository;
    private final RemountDate remountDate;
    private final AndroidMqttClient androidMqttClient;
    private final AdvertisementContract.View view;
    private final Handler mBackgroundHandler;
    private IpcPresenter ipcPresenter;

    private Handler handler;
    private ConnectivityChangedReceiver receiver;
    private AdTaskExecutor adTaskExecutor;
    private Context context;

    private Runnable sendHeartbeatPackage = new Runnable() {
        @Override
        public void run() {
            androidMqttClient.publishHeartbeatPackage();
            handler.postDelayed(sendHeartbeatPackage, TimeUnit.SECONDS.toMillis(10));
        }
    };

    private Runnable uploadScreenshot = new Runnable() {
        @Override
        public void run() {
            Bitmap bitmap = view.takeScreenshot();
            Log.d("AdvertisementPresenter", "begin to send screenShot");
            androidMqttClient.publishScreenshot(bitmap, new OnUpLoadInfoCallBack() {

                @Override
                public void onUpLoadSuccess() {
                    mBackgroundHandler.removeCallbacks(uploadScreenshot);
                    mBackgroundHandler.postDelayed(uploadScreenshot, TimeUnit.MINUTES.toMillis(30));
                }

                @Override
                public void onUpLoadFail() {
                    mBackgroundHandler.removeCallbacks(uploadScreenshot);
                    mBackgroundHandler.postDelayed(uploadScreenshot, TimeUnit.MINUTES.toMillis(30));
                    Voyage.e("AdvertisementPresenter", "上传截图失败");
                }
            });
        }
    };

    private Runnable sendVersionInfo = new Runnable() {
        @Override
        public void run() {
            androidMqttClient.publishVersionInfo(new OnUpLoadInfoCallBack() {
                @Override
                public void onUpLoadSuccess() {
                    SharedUtils.putString(SHARED_NAME, context, "VersionName", BuildConfig.VERSION_NAME);
                }

                @Override
                public void onUpLoadFail() {
                    handler.postDelayed(sendVersionInfo, TimeUnit.SECONDS.toMillis(10));
                }
            });
        }
    };

    AdvertisementPresenter(
            @NonNull AdvertisementContract.View view,
            @NonNull AdsRepository adsRepository,
            @NonNull MonitorsRepository monitorsRepository,
            @NonNull SchedulesRepository schedulesRepository,
            @NonNull RemountDate remountDate,
            @NonNull AndroidMqttClient androidMqttClient,
            Context context) {
        this.view = view;
        this.view.setPresenter(this);
        this.adsRepository = adsRepository;
        this.monitorsRepository = monitorsRepository;
        this.schedulesRepository = schedulesRepository;
        this.remountDate = remountDate;
        this.androidMqttClient = androidMqttClient;
        this.context = context;
        HandlerThread handlerThread = new HandlerThread("-uploadScreenshot");
        handlerThread.start();
        mBackgroundHandler = new Handler(handlerThread.getLooper());
        handler = new Handler(new Callback(this));
        ipcPresenter = MyIpcPresenter.getInstance(context);
    }

    @Override
    public void start() {
        handler.postDelayed(sendHeartbeatPackage, TimeUnit.SECONDS.toMillis(5));

        mBackgroundHandler.removeCallbacks(uploadScreenshot);
        mBackgroundHandler.postDelayed(uploadScreenshot, TimeUnit.MINUTES.toMillis(30));

        receiver = new ConnectivityChangedReceiver(androidMqttClient);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        view.registerReceiver(receiver, filter);


        EventBus.getDefault().register(this);

        CyclicPlayer.View cyclicPlayerView = new CyclicPlayerViewAdapter(view);
        CyclicPlayer player = new CyclicPlayer(cyclicPlayerView);
        adTaskExecutor = new AdTaskExecutor(player);

        String lastVersionName = SharedUtils.getString(SHARED_NAME, context, "VersionName");
        if (!BuildConfig.VERSION_NAME.equals(lastVersionName))
            handler.postDelayed(sendVersionInfo, TimeUnit.SECONDS.toMillis(10));

        updateAdsRepository();
        updateRemountDate();

    }

    private void updateRepositories() {
        updateAdsRepository();
        updateMonitorsRepository();
        updateSchedulesRepository();
    }

    private void updateRemountDate() {
        remountDate.getDateFromRemount(new SyncDateCallBack() {
            @Override
            public void onDateIsTheSame(TimeResponse.Date date) {
                Log.d("AdvertisementPresenter", "设备端和服务器时间一致");
            }

            @Override
            public void onDateIsDifferent(TimeResponse.Date date) {
                JSONArray cmdArray = new JSONArray();
                cmdArray.put("setprop persist.sys.timezone " + date.zone);
                String s2 = "\""+date.time.substring(0,8) + '.' + date.time.substring(8,date.time.length())+"\"";
                cmdArray.put("date -s " + s2);
                String cmdStr = cmdArray.toString();
                Log.e("updateRemountDate", "run: " + cmdStr);
                ipcPresenter.sendMessage(cmdStr);
            }
        });
    }

    private void updateAdsRepository() {
        adsRepository.update(new SynchronizeAdsCallback() {
            @Override
            public void onPreAdsSynchronize() {
                handler.sendEmptyMessage(MSG_TYPE_SHOW_SYNCHRONIZING_STATUS);
            }

            @Override
            public void onAdsSynchronized(List<Ad> ads) {
                adTaskExecutor.execute(ads);
            }

            @Override
            public void onRemoteDataNotAvailable(List<Ad> ads) {
                Message.obtain(handler, MSG_TYPE_SHOW_MESSAGE, view.getString(R.string.sync_fail)).sendToTarget();

                adTaskExecutor.execute(ads);
            }
        });
    }

    private void updateMonitorsRepository() {
        monitorsRepository.update(new SynchronizeMonitorsCallback() {
            @Override
            public void onMonitorsSynchronized(Monitors monitors) {
                Message.obtain(handler, MSG_TYPE_SET_MONITOR, monitors).sendToTarget();
            }

            @Override
            public void onRemoteDataNotAvailable(Monitors monitors) {
                Message.obtain(handler, MSG_TYPE_SET_MONITOR, monitors).sendToTarget();
            }
        });
    }

    private void updateSchedulesRepository() {
        schedulesRepository.update(new SynchronizeSchedulesCallback() {
            @Override
            public void onSchedulesSynchronized(List<ScheduleRecord> records) {
                Message.obtain(handler, MSG_TYPE_SET_SCHEDULE, records).sendToTarget();
            }

            @Override
            public void onRemoteDataNotAvailable(List<ScheduleRecord> records) {
                Message.obtain(handler, MSG_TYPE_SET_SCHEDULE, records).sendToTarget();
            }
        });
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommandReceived(PublishNews command) {
        view.showBulletin(command.to());
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommandReceived(CancelHeadline cancelHeadline) {
        view.cancelBulletin();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommandReceived(CancelAd cancelAd) {
        updateAdsRepository();
        view.showMessage(cancelAd.id + view.getString(R.string.ad_delete));
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommandReceived(SetVolumeCommand setVolumeCommand) {
        view.setVolume(setVolumeCommand.volume);
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommandReceived(Advertise advertise) {
        updateAdsRepository();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommandReceived(final MonitorsCommand command) {
        view.showMessage(view.getString(R.string.update_monitor));
        updateMonitorsRepository();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommandReceived(ScheduleSettings command) {
        updateSchedulesRepository();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommandReceived(RestartCommand command) {
        view.restart();
    }

    private void setSchedule(List<ScheduleRecord> records) {
        if (records.isEmpty())
            return;
        long powerOnMillis = TSchedule.getLatestPowerOnMillis(records);
        Power.onAtTime(powerOnMillis);
        long powerOffMillis = TSchedule.getLatestPowerOffMillis(records);
        Power.offAtTime(powerOffMillis);
    }

    @Override
    public void stop() {
        EventBus.getDefault().unregister(this);
        view.unregisterReceiver(receiver);
        mBackgroundHandler.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onNetworkActiveListener() {
        updateRepositories();
    }

    private static class Callback implements Handler.Callback {
        WeakReference<AdvertisementPresenter> reference;

        private Callback(AdvertisementPresenter presenter) {
            reference = new WeakReference<>(presenter);
        }

        @Override
        public boolean handleMessage(Message msg) {
            AdvertisementPresenter presenter = reference.get();
            if (presenter == null)
                return false;

            if (msg.what == MSG_TYPE_SHOW_MESSAGE) {
                presenter.view.showMessage(msg.obj.toString());
            } else if (msg.what == MSG_TYPE_SHOW_SYNCHRONIZING_STATUS) {
                presenter.view.synchronizing();
            } else if (msg.what == MSG_TYPE_SET_MONITOR) {
                Monitors monitors = (Monitors) msg.obj;
                presenter.view.startMonitorPlayerAsync(monitors.ips, monitors.duration);
            } else if (msg.what == MSG_TYPE_SET_SCHEDULE) {
                presenter.setSchedule((List<ScheduleRecord>) msg.obj);
            } else
                return false;

            return true;
        }
    }
}

