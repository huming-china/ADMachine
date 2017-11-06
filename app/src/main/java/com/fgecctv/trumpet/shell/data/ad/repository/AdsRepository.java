package com.fgecctv.trumpet.shell.data.ad.repository;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.android.volley.toolbox.RequestFuture;
import com.fgecctv.io.Files;
import com.fgecctv.trumpet.shell.app.AppEnvironment;
import com.fgecctv.trumpet.shell.network.http.AdDownloadListener;
import com.fgecctv.trumpet.shell.network.http.AndroidHttpClient;
import com.fgecctv.trumpet.shell.network.http.EncryptedGetRequest;
import com.fgecctv.trumpet.shell.network.http.request.ProgressOfDownload;
import com.fgecctv.trumpet.shell.network.http.request.SyncRequestBody;
import com.fgecctv.trumpet.shell.network.http.response.RemoteResources;
import com.squirrel.voyage.Voyage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdsRepository {

    private static final String TAG = "AdsRepository";

    private static AdsRepository instance;
    private final AndroidHttpClient httpClient;
    private final AdsDataSource adsDataBase;
    private final AdFetcher fetcher;
    private final String id;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private AdDownloadListener listener = new AdDownloadListener() {
        @Override
        public void onProgress(String adId, float percentage) {
            if ((int) (percentage * 100 % 10) != 0)
                return;

            ProgressOfDownload progress = new ProgressOfDownload();
            progress.terminalId = id;
            progress.programId = adId;
            progress.progress = String.format(Locale.getDefault(), "%d%%", (int) (percentage * 100));
            httpClient.sendRequest(new EncryptedGetRequest(progress));
        }
    };

    private AdsRepository(Context context, String id) {
        this.id = id;
        this.adsDataBase = new AdsDataBase(context);
        this.httpClient = AndroidHttpClient.getInstance(context);
        this.fetcher = new AdFetcher();
    }

    public static synchronized AdsRepository getInstance(Context context, String id) {
        if (instance == null)
            instance = new AdsRepository(context, id);
        return instance;
    }

    public void update(final SynchronizeAdsCallback callback) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                synchronize(callback);
            }
        });
    }

    private synchronized void synchronize(final SynchronizeAdsCallback callback) {
        List<RemoteResources.Ad> remoteAds;

        callback.onPreAdsSynchronize();

        try {
            remoteAds = getRemoteAdResources();
        } catch (SynchronizingException e) {
            callback.onRemoteDataNotAvailable(query());
            return;
        }

        deleteRedundantAds(remoteAds);

        for (RemoteResources.Ad r : remoteAds)
            try {
                adsDataBase.delete(r.id);
                fetcher.fetch(new AdResource(r.id, r.url), listener, false);
            } catch (IOException e) {
                Voyage.e(TAG, e.getMessage() + " " + r.url);
                callback.onRemoteDataNotAvailable(query());
                return;
            }

        List<Ad> localAds = new ArrayList<>();

        for (RemoteResources.Ad r : remoteAds)
            localAds.add(new Ad.Builder(r.id)
                    .setEffectiveDate(r.getEffectiveDate())
                    .setExpiryDate(r.getExpiryDate())
                    .setRepeats(r.repeats)
                    .setPriority(r.getPriority())
                    .setTimestamp(r.getTimestamp())
                    .create());

        for (Ad a : localAds)
            adsDataBase.insert(new AdRecord.Builder(a).create());

        callback.onAdsSynchronized(localAds);
    }

    private void deleteRedundantAds(List<RemoteResources.Ad> remoteAds) {
        deleteAdsWhichIsNotInLocalDatabase();
        deleteAdsWhichHasBeenDeletedOrModifiedOnServer(remoteAds);
    }

    private void deleteAdsWhichHasBeenDeletedOrModifiedOnServer(List<RemoteResources.Ad> remoteAds) {
        List<Ad> redundantAds = getRedundantAds(remoteAds);
        for (Ad ad : redundantAds)
            delete(ad.getId());
    }

    private void deleteAdsWhichIsNotInLocalDatabase() {
        List<File> adsNotInLocalDatabase = new ArrayList<>();
        File[] localAdsDirectories = getAdsFile().listFiles() == null ? new File[0] : getAdsFile().listFiles();
        Collections.addAll(adsNotInLocalDatabase, localAdsDirectories);
        for (Ad ad : query())
            for (int i = 0; i < adsNotInLocalDatabase.size(); i++)
                if (ad.getId().equals(adsNotInLocalDatabase.get(i).getName()))
                    adsNotInLocalDatabase.remove(i);

        for (File file : adsNotInLocalDatabase)
            Files.delete(file);
    }

    @NonNull
    private List<Ad> getRedundantAds(List<RemoteResources.Ad> remoteAds) {
        List<Ad> ads = query();
        for (RemoteResources.Ad r : remoteAds)
            for (int i = 0; i < ads.size(); i++)
                if (ads.get(i).getId().equals(r.id) && ads.get(i).getTimestamp() == r.getTimestamp())
                    ads.remove(i);

        return ads;
    }

    @NonNull
    private List<RemoteResources.Ad> getRemoteAdResources() throws SynchronizingException {
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

        Log.v(TAG, response);

        RemoteResources remoteResources = JSON.parseObject(response, RemoteResources.class);

        return remoteResources.ads;
    }

    private void delete(String id) {
        adsDataBase.delete(id);

        Ad ad = new Ad.Builder(id).create();
        ad.delete();
    }

    private List<Ad> query() {
        ArrayList<Ad> ads = new ArrayList<>();
        for (AdRecord r : adsDataBase.query())
            ads.add(new Ad.Builder(r).create());
        return ads;
    }

    @NonNull
    private static File getAdsFile() {
        return new File(AppEnvironment.getExternalStoragePath() + File.separator + "Ads");
    }
}