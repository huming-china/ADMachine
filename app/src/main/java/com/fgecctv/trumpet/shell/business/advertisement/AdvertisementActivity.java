package com.fgecctv.trumpet.shell.business.advertisement;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.fgecctv.ipc_client.IpcPresenter;
import com.fgecctv.trumpet.shell.BuildConfig;
import com.fgecctv.trumpet.shell.R;
import com.fgecctv.trumpet.shell.business.update.SelfUpdate;
import com.fgecctv.trumpet.shell.business.upload.MyIpcPresenter;
import com.fgecctv.trumpet.shell.data.ad.repository.AdsRepository;
import com.fgecctv.trumpet.shell.data.date.RemountDate;
import com.fgecctv.trumpet.shell.data.monitor.MonitorsRepository;
import com.fgecctv.trumpet.shell.data.schedule.SchedulesRepository;
import com.fgecctv.trumpet.shell.network.mqtt.AndroidMqttClient;
import com.squirrel.voyage.Voyage;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AdvertisementActivity extends AppCompatActivity {

    private final int START_PROCESS_GUARD = 0;
    private final int STOP_PROCESS_GUARD = 1;

    @Bind(R.id.textViewDeviceId)
    TextView textViewDeviceId;
    @Bind(R.id.textViewVersionName)
    TextView textViewVersionName;

    private SelfUpdate selfUpdate;
    private IpcPresenter ipcPresenter;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            JSONObject object = new JSONObject();
            String packageName = getPackageName();
            if (msg.what == START_PROCESS_GUARD) {
                object.put("type", "start_process_guard");
                object.put("data", packageName);
                ipcPresenter.sendMessage(object.toString());
            } else if (msg.what == STOP_PROCESS_GUARD) {
                object.put("type", "stop_process_guard");
                object.put("data", packageName);
                ipcPresenter.sendMessage(object.toString());
            }
            return false;
        }
    });

    private void enterImmersiveMode() {
        View decorView = getWindow().getDecorView();

        int newUiOptions = decorView.getSystemUiVisibility();

        newUiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setSystemUiVisibility(newUiOptions);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertising);
        ButterKnife.bind(this);

        ipcPresenter = MyIpcPresenter.getInstance(this);
        if (!BuildConfig.DEBUG)
            handler.sendEmptyMessageDelayed(START_PROCESS_GUARD, 5000);

        AdvertisementFragment advertisementFragment = (AdvertisementFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (advertisementFragment == null) {
            // Create the fragment
            advertisementFragment = AdvertisementFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame, advertisementFragment);
            transaction.commit();
        }

        Intent intent = getIntent();
        String deviceId = intent.getStringExtra("DeviceId");
        textViewDeviceId.setHint(getString(R.string.device) + " ID: " + deviceId);
        textViewVersionName.setHint(getString(R.string.version) + ": " + BuildConfig.VERSION_NAME);

        MonitorsRepository monitorsRepository = MonitorsRepository.getInstance(getApplicationContext(), deviceId);
        AdsRepository adsRepository = AdsRepository.getInstance(getApplicationContext(), deviceId);
        SchedulesRepository schedulesRepository = SchedulesRepository.getInstance(getApplicationContext(), deviceId);
        AndroidMqttClient mqttClient = AndroidMqttClient.getInstance(getApplicationContext(), deviceId);
        RemountDate remountDate = RemountDate.getInstance(getApplicationContext(),deviceId);

        selfUpdate = new SelfUpdate();
        selfUpdate.checkUpdate(AdvertisementActivity.this);

        new AdvertisementPresenter(advertisementFragment,
                adsRepository,
                monitorsRepository,
                schedulesRepository,
                remountDate,
                mqttClient,
                this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        enterImmersiveMode();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        selfUpdate.onDestroy();

        if (ipcPresenter != null) {
            try {
                ipcPresenter.stop();
            } catch (Exception e) {
                Voyage.e("ipcPresenter解绑失败", e);
            }
        }
    }

    public void cancelGuard(View view) {
        handler.sendEmptyMessage(STOP_PROCESS_GUARD);
    }
}