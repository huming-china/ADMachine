package com.fgecctv.trumpet.shell.data.date;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.android.volley.toolbox.RequestFuture;
import com.fgecctv.trumpet.shell.network.http.AndroidHttpClient;
import com.fgecctv.trumpet.shell.network.http.EncryptedGetRequest;
import com.fgecctv.trumpet.shell.network.http.request.TimeRequestBody;
import com.fgecctv.trumpet.shell.network.http.response.TimeResponse;

import java.math.BigDecimal;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RemountDate {

    private static RemountDate instance;
    private final AndroidHttpClient httpClient;
    private String id;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private RemountDate (Context context, String id) {
        this.httpClient = AndroidHttpClient.getInstance(context);
        this.id = id;
    }

    public static synchronized RemountDate getInstance(Context context, String id) {
        if (instance == null)
            instance = new RemountDate(context, id);
        return instance;
    }

    public void getDateFromRemount(final SyncDateCallBack callBack) {
        final TimeResponse.Date date = new TimeResponse.Date();

        executorService.submit(new Runnable() {
           @Override
           public void run() {
               final RequestFuture<String> future = RequestFuture.newFuture();
               final TimeRequestBody request = new TimeRequestBody();
               request.terminalId = id;

               httpClient.sendRequest(new EncryptedGetRequest(request, future, future));

               String response = null;
               try {
                   response = future.get();
               } catch (InterruptedException e) {
                   e.printStackTrace();
               } catch (ExecutionException e) {
                   e.printStackTrace();
               }

               TimeResponse remoteResources = JSON.parseObject(response, TimeResponse.class);

               date.time = remoteResources.data;
               date.zone = remoteResources.zone;
               Log.d("RemountDate", (date.zone + "/" + date.time));

               long remount = TimeUtils.getLongTime(date.time,"yyyyMMddHHmmss");
               BigDecimal b1 = new BigDecimal(System.currentTimeMillis());
               BigDecimal b2 = new BigDecimal(remount);
               long abs = Math.abs(b1.subtract(b2).longValue());

               if (TimeZone.getDefault().getID().equals(date.zone)) {
                   if (abs <= TimeUnit.MINUTES.toMillis(5)) {
                       callBack.onDateIsTheSame(date);
                   } else {
                       callBack.onDateIsDifferent(date);
                   }
               }else {
                   callBack.onDateIsDifferent(date);
               }
           }
       });

    }

}
