package com.fgecctv.trumpet.shell.network.http;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fgecctv.secure.encrypt.des.Des;
import com.fgecctv.trumpet.shell.network.http.request.RequestBody;
import com.google.common.base.Preconditions;
import com.squirrel.voyage.Voyage;

import java.util.HashMap;
import java.util.Map;

public class EncryptedGetRequest extends StringRequest {
    private static final String URL;
    private static final String TAG = "EncryptedGetRequest";
    private static Response.Listener<String> defaultListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
        }
    };
    private static Response.ErrorListener defaultErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
        }
    };

    static {
        URL = "http://ad.cloudring.net/CloudringAD/proxy/third?params=";
    }

    public EncryptedGetRequest(RequestBody body, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.GET, URL + encryptRequestBody(body), listener, errorListener);
    }

    public EncryptedGetRequest(RequestBody body) {
        super(Method.GET, URL + encryptRequestBody(body), defaultListener, defaultErrorListener);
    }

    private static String encryptRequestBody(RequestBody body) {
        Preconditions.checkNotNull(body);

        String requestBody = JSON.toJSONString(body);
        try {
            return Des.encrypt(requestBody);
        } catch (Exception e) {
            Voyage.e(TAG, "加密失败: " + requestBody);
        }
        return "";
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> map = new HashMap<>(1);
        map.put("apikey", "!@#$cloudringbox!@#$");
        return map;
    }
}
