package com.ssafy.moa2zi.worker;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.webkit.CookieManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.ssafy.moa2zi.R;
import com.ssafy.moa2zi.common.http.HttpClient;
import com.ssafy.moa2zi.common.http.ResponseWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieStore;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LocationWorker extends Worker {

    private final static String NOTIFICATION_URL= "https://j12a403.p.ssafy.io/api/v1/notifications/android";
    public LocationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        try {
            Context context = getApplicationContext();

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return Result.failure();
            }

            Task<Location> locationTask = fusedLocationClient.getLastLocation();
            Tasks.await(locationTask); // 동기적으로 위치 얻기 (주의: 메인 스레드 X)

            Location location = locationTask.getResult();
            if (location != null) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();


                sendLocationToServer(lat, lng); // 서버에 POST
                return Result.success();
            } else {
                return Result.retry(); // 위치를 못 얻었으면 나중에 재시도
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }

    private void sendLocationToServer(double latitude, double longitude) throws JSONException {


        HttpClient httpClient = new HttpClient();

        Map<String, Float> request = new HashMap<>();
        request.put("latitude", (float) latitude);
        request.put("longitude", (float) longitude);

        ResponseWrapper response = httpClient.sentToServerWithCookie("POST", "notifications/android", request);

        if(response.getStatusCode() == 200){
            showNotification("모앗쥐", response.getBody().getString("content"));
        }

    }


    private void showNotification(String title, String message) {

        Context context = getApplicationContext();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "default";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_HIGH // 중요도 높게 설정
            );
            channel.setDescription("기본 알림 채널입니다.");
            channel.enableLights(true);
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);
        }


        // Oreo 이상은 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_notification)
                .setAutoCancel(true);

        manager.notify(1, builder.build());
    }
}
