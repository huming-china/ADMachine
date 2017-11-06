package com.fgecctv.trumpet.shell.data.monitor;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.android.volley.toolbox.RequestFuture;
import com.fgecctv.trumpet.shell.data.ad.repository.SynchronizingException;
import com.fgecctv.trumpet.shell.network.http.AndroidHttpClient;
import com.fgecctv.trumpet.shell.network.http.EncryptedGetRequest;
import com.fgecctv.trumpet.shell.network.http.request.SyncRequestBody;
import com.fgecctv.trumpet.shell.network.http.response.RemoteResources;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MonitorsRepository {

    private static MonitorsRepository instance;
    private final MonitorsDatabase monitorsDatabase;
    private final String id;
    private final AndroidHttpClient httpClient;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private MonitorsRepository(Context context, String id) {
        this.monitorsDatabase = new MonitorsDatabase(context);
        this.httpClient = AndroidHttpClient.getInstance(context);
        this.id = id;
    }

    public static synchronized MonitorsRepository getInstance(Context context, String id) {
        if (instance == null)
            instance = new MonitorsRepository(context, id);
        return instance;
    }

    public void update(final SynchronizeMonitorsCallback callback) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                synchronize(callback);
            }
        });
    }

    private void synchronize(SynchronizeMonitorsCallback callback) {
        Monitors monitors = new Monitors();
        monitors.ips = new ArrayList<>();

        try {
            List<RemoteResources.Monitor> remoteMonitors = getRemoteMonitors();

            monitorsDatabase.deleteAll();
            List<MonitorRecord> localMonitors = new ArrayList<>();

            for (RemoteResources.Monitor m : remoteMonitors) {
                monitors.ips.add(m.ip);
                monitors.duration = m.transitTime;
                localMonitors.add(new MonitorRecord.Builder()
                        .setIp(m.ip)
                        .setDuration(m.transitTime)
                        .create());
            }

            monitorsDatabase.insert(localMonitors);
            callback.onMonitorsSynchronized(monitors);
        } catch (SynchronizingException e) {
            for (MonitorRecord r : monitorsDatabase.query()) {
                monitors.ips.add(r.ip);
                monitors.duration = r.transitTime;
            }

            callback.onRemoteDataNotAvailable(monitors);
        }
    }

    private List<RemoteResources.Monitor> getRemoteMonitors() throws SynchronizingException {
        RequestFuture<String> future = RequestFuture.newFuture();
        SyncRequestBody request = new SyncRequestBody();
        request.terminalId = id;
        httpClient.sendRequest(new EncryptedGetRequest(request, future, future));

        String response;

        try {
            response = future.get();
        } catch (InterruptedException e) {
            throw new AssertionError("Fetching remote resources shouldn't be interrupted.");
        } catch (ExecutionException e) {
            throw new SynchronizingException("Network is unavailable currently.");
        }

        RemoteResources remoteResources = JSON.parseObject(response, RemoteResources.class);

        return remoteResources.monitors;
    }
}
