package com.fgecctv.trumpet.shell.business.bind;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fgecctv.trumpet.shell.BuildConfig;
import com.fgecctv.trumpet.shell.R;
import com.fgecctv.trumpet.shell.business.advertisement.AdvertisementActivity;
import com.fgecctv.trumpet.shell.network.http.AndroidHttpClient;
import com.fgecctv.trumpet.shell.network.utils.NetWorkUtils;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BindActivity extends AppCompatActivity implements BindUserInterface {

    private static final String TAG = "BindActivity";

    @Bind(R.id.account)
    EditText accountView;
    @Bind(R.id.password)
    EditText passwordView;
    @Bind(R.id.deviceName)
    EditText deviceNameView;
    @Bind(R.id.bind_button)
    Button bindButton;
    @Bind(R.id.textViewDeviceId)
    TextView textViewDeviceId;
    @Bind(R.id.textViewVersionName)
    TextView textViewVersion;

    private Presenter presenter;
    private String deviceId;
    private String KEY_BINDING_STATUS = "bindingStatus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);
        ButterKnife.bind(this);

        if (!NetWorkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.netWork_is_not_connected), Toast.LENGTH_LONG).show();
        }

        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        textViewDeviceId.setHint(getString(R.string.device) + " ID: " + deviceId.toUpperCase());
        textViewVersion.setHint(BuildConfig.VERSION_NAME);
        AndroidHttpClient httpClient = AndroidHttpClient.getInstance(getApplicationContext());
        presenter = new Presenter(this, deviceId, httpClient);


        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);

        if (getBindingStatus())
            showAdvertisement();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.bind_button)
    void bind() {
        presenter.bind(
                deviceNameView.getText().toString(),
                accountView.getText().toString(),
                passwordView.getText().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onLoginSuccess() {
        showAdvertisement();
    }

    @Override
    public void onLoginFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        bindButton.setClickable(true);
        bindButton.setText(R.string.login);
    }

    @Override
    public void loading() {
        bindButton.setClickable(false);
        bindButton.setText(R.string.binding);
    }

    @Override
    public void showAdvertisement() {
        Intent intent = new Intent(this, AdvertisementActivity.class);
        intent.putExtra("DeviceId", deviceId);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean getBindingStatus() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        return preferences.getBoolean(KEY_BINDING_STATUS, false);
    }

    @Override
    public void setBindingStatus(boolean state) {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        preferences.edit().putBoolean(KEY_BINDING_STATUS, state).apply();
    }
}
