package com.fgecctv.trumpet.shell.business.update;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.fgecctv.trumpet.shell.BuildConfig;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

 class CheckVersionUtils {

    //获取服务器上最新版本
    private static String getNewestVersion() {
        HttpURLConnection conn = null;
        InputStream in = null;
        String jsonString = "";
        try {
            URL url = new URL(UpdateConstants.AD_UPDATE_URL);
            String param = "type=" + URLEncoder.encode(UpdateConstants.AD_UPDATE_TYPE);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();

            //建立输入流，向指向的URL传入参数
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(param);
            dos.flush();
            dos.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    jsonString += line;
                }
                Log.e("CheckVersionUtils", jsonString);
            }
        }
         catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null )
            conn.disconnect();
        }
        return jsonString;
    }

    VersionUpdateBean getVersionUpdateBean() {
        String jsonString = CheckVersionUtils.getNewestVersion();
        VersionUpdateBean versionUpdateBean = null;
        if (jsonString != null && jsonString.length() > 0) {
            versionUpdateBean = JSON.parseObject(jsonString, VersionUpdateBean.class);
        }
        return versionUpdateBean;
    }

    boolean hasNewVersion() {
        int localInt = BuildConfig.VERSION_CODE;
        VersionUpdateBean versionUpdateBean = getVersionUpdateBean();
        if (versionUpdateBean != null) {
            String versionNumberFormat = versionUpdateBean.versionNumber;
            if (TextUtils.isEmpty(versionNumberFormat)) {
                return false;
            }
            Integer netInt = Integer.parseInt(versionNumberFormat);
            //有新版本
            return netInt > localInt;
        }
        return false;
    }
}
