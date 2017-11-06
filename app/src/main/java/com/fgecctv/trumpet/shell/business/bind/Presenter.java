package com.fgecctv.trumpet.shell.business.bind;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fgecctv.secure.encrypt.md5.Encryption;
import com.fgecctv.trumpet.shell.BuildConfig;
import com.fgecctv.trumpet.shell.network.http.AndroidHttpClient;
import com.fgecctv.trumpet.shell.network.http.EncryptedGetRequest;
import com.fgecctv.trumpet.shell.network.http.request.BindRequestBody;
import com.fgecctv.trumpet.shell.network.http.response.BindResponse;

class Presenter implements Response.ErrorListener, Response.Listener<String> {
    @SuppressWarnings("unused")
    private static final String TAG = "Presenter";
    private final AndroidHttpClient androidHttpClient;
    private BindUserInterface userInterface;
    private String androidId;

    Presenter(BindUserInterface userInterface, String androidId, AndroidHttpClient httpClient) {
        this.userInterface = userInterface;
        this.androidId = androidId;
        this.androidHttpClient = httpClient;
    }

    public void bind(String deviceName, String account, String password) {
        if (loginParamsIllegal(deviceName, account, password)) {
            userInterface.onLoginFailure("Device Name, Username and Password Can't be Empty");
            return;
        }

        userInterface.loading();

        BindRequestBody bindRequestBody = new BindRequestBody();
        bindRequestBody.id = androidId;
        bindRequestBody.name = deviceName;
        bindRequestBody.version = BuildConfig.VERSION_NAME;
        bindRequestBody.userCode = account;
        bindRequestBody.password = Encryption.MD5(password);

        androidHttpClient.sendRequest(new EncryptedGetRequest(bindRequestBody, this, this));
    }

    private boolean loginParamsIllegal(CharSequence deviceName, CharSequence account, CharSequence password) {
        return deviceName.length() == 0 || account.length() == 0 || password.length() == 0;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        userInterface.onLoginFailure("The network is currently unavailable");
    }

    @Override
    public void onResponse(String response) {
        BindResponse bindResponse = BindResponse.createFromJson(response);
        if (bindResponse.success()) {
            userInterface.onLoginSuccess();
            userInterface.setBindingStatus(true);
        } else
            userInterface.onLoginFailure(bindResponse.message);
    }
}
