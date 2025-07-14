package com.ssafy.moa2zi;

import android.app.Application;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.work.Configuration;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.ssafy.moa2zi.worker.LocationWorker;

import java.util.concurrent.TimeUnit;


public class MyApp extends Application implements Configuration.Provider {
    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setMinimumLoggingLevel(Log.DEBUG)
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(LocationWorker.class).build();
//        WorkManager.getInstance(this).enqueue(request);

        PeriodicWorkRequest locationWork =
                new PeriodicWorkRequest.Builder(LocationWorker.class, 15, TimeUnit.MINUTES)
                        .setConstraints(new Constraints.Builder().build())
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "location_work",
                ExistingPeriodicWorkPolicy.UPDATE,
                locationWork
        );

    }


}
