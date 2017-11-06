package com.fgecctv.trumpet.shell.network.http;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.common.base.Preconditions;

public class AndroidHttpClient {
    private static AndroidHttpClient instance;
    private RequestQueue requestQueue;

    private AndroidHttpClient(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public synchronized static AndroidHttpClient getInstance(Context context) {
        if (instance == null)
            instance = new AndroidHttpClient(context);
        return instance;
    }

    public synchronized void sendRequest(EncryptedGetRequest request) {
        Preconditions.checkNotNull(request);
        requestQueue.add(request);
    }
}
