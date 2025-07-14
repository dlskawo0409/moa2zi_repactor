package com.ssafy.moa2zi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ssafy.moa2zi.common.http.HttpClient;
import com.ssafy.moa2zi.common.http.ResponseWrapper;
import com.ssafy.moa2zi.worker.LocationWorker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int REQUEST_BACKGROUND_LOCATION_CODE = 1002;

    private static final String PREFS_NAME = "moa2zi_prefs";
    private static final String KEY_SKIP_LOCATION_DIALOG = "skip_location_dialog";
    private static final String KEY_BACKGROUND_LOCATION_SHOWN = "background_location_shown";
    private static final String KEY_NOTIFICATION_SHOWN = "notification_shown";
    private final String SpringServerUrl = "https://j12a403.p.ssafy.io";
    private static final String KEY_FCM_TOKEN = "fcm_token";
    private static final String KEY_FCM_SUBMIT = "fcm_submit";
    private ActivityResultLauncher<Intent> appSettingsLauncher;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean skipDialog = prefs.getBoolean(KEY_SKIP_LOCATION_DIALOG,false);

        appSettingsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean(KEY_BACKGROUND_LOCATION_SHOWN, true);
                    editor.commit();

                    checkNotificationPermission();
                }
        );

//        FirebaseApp.initializeApp(this); // 🔥 이걸 추가

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        String cookies = CookieManager.getInstance().getCookie(SpringServerUrl);
        boolean fcmSubmit = prefs.getBoolean(KEY_FCM_SUBMIT,false);

        if (cookies != null && cookies.contains("refresh") && !fcmSubmit ) {
            String token = prefs.getString(KEY_FCM_TOKEN, null);

            Log.d("FCM", "fcm Token : " + token);

            if (token != null) {
                Log.d("FLOW", "start send");
                new Thread(() -> {
                    sendFCMTokenToServer(token);
                }).start();
            }
            else{
                getFCMTokenAndSave();
            }
        }



        webView = new WebView(this);
        setContentView(webView);

        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);

// 1. WebViewClient 설정 (페이지 로딩, 쿠키 등 담당)
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                String cookies = CookieManager.getInstance().getCookie(url);
                Log.d("쿠키확인", "onPageFinished: " + cookies);
                cookieManager.flush();

            }
        });

// 2. WebChromeClient 설정 (위치 권한, 알림창 등 담당)
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                // 항상 위치 권한 허용
                callback.invoke(origin, true, false);
            }
        });

        webView.loadUrl("https://j12a403.p.ssafy.io");
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d("BACK", "뒤로 가기 버튼 눌림");

                if (webView.canGoBack()) {
                    Log.d("BACK", "WebView 뒤로 가기 가능 → goBack() 호출");
                    webView.goBack();
                } else {
                    Log.d("BACK", "WebView 뒤로 가기 불가 → 기본 뒤로 가기 실행");
                    setEnabled(false); // 콜백 비활성화
                    getOnBackPressedDispatcher().onBackPressed(); // 기본 동작 실행
                }
            }
        });
        if(!skipDialog){
            showLocationPermissionExplanation();
        }

    }

    private void showLocationPermissionExplanation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            checkBackgroundLocationPermission();
            return;
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean skipDialog = prefs.getBoolean(KEY_SKIP_LOCATION_DIALOG, false);

        if (!skipDialog) {
            final View checkBoxLayout = getLayoutInflater().inflate(R.layout.dialog_checkbox, null);
            final CheckBox dontShowAgainCheckBox = checkBoxLayout.findViewById(R.id.checkbox_dont_show_again);

            new AlertDialog.Builder(this)
                    .setTitle("위치 권한 필요")
                    .setMessage("정확한 위치 정보를 사용하여 위치 기반 알람을 제공하기 위해서는 위치 권한이 필요합니다.")
                    .setView(checkBoxLayout)
                    .setPositiveButton("허용", (dialog, which) -> {
                        saveSkipDialogPreference(dontShowAgainCheckBox.isChecked());
                        requestFineLocation(); // 권한 요청
                    })
                    .setNegativeButton("거절", (dialog, which) -> {
                        saveSkipDialogPreference(dontShowAgainCheckBox.isChecked());
                    })
                    .show();
        }
    }

    private void saveSkipDialogPreference(boolean shouldSkip) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(KEY_SKIP_LOCATION_DIALOG, shouldSkip);
        editor.apply();
    }


    private void requestFineLocation() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean skipDialog = prefs.getBoolean(KEY_SKIP_LOCATION_DIALOG, false);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(!skipDialog){
                    requestBackgroundLocation();
                }
            } else {
                Toast.makeText(this, "위치 권한을 허용해야 알림을 받을 수 있습니다.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_BACKGROUND_LOCATION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED  ) {
                checkNotificationPermission(); // 3단계
            } else {
                    goToAppSettings();
            }
        } else if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationWorker(); // 최종
            } else {
                Toast.makeText(this, "알림 권한이 거부되었습니다. 설정에서 수동으로 변경할 수 있습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void requestBackgroundLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            new AlertDialog.Builder(this)
                    .setTitle("항상 허용 필요")
                    .setMessage("위치 알람을 위해 '항상 허용'으로 설정이 필요합니다.\n\n[설정 → 권한 → 위치 → 항상 허용] 으로 변경해주세요.")
                    .setPositiveButton("허용", (dialog, which) -> ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                            REQUEST_BACKGROUND_LOCATION_CODE))
                    .setNegativeButton("취소", null)
                    .show();
        } else {
            checkNotificationPermission();
        }
    }


    private void checkBackgroundLocationPermission() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean backgroundShown = prefs.getBoolean(KEY_BACKGROUND_LOCATION_SHOWN, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && !backgroundShown ) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        REQUEST_BACKGROUND_LOCATION_CODE);
            } else {
                checkNotificationPermission();
            }
        } else {
            checkNotificationPermission();
        }
    }

    private void checkNotificationPermission(){
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean notificationShown = prefs.getBoolean(KEY_NOTIFICATION_SHOWN, false);
        Log.d("NOTIFICATION","notification : " + notificationShown );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                && !notificationShown) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);

                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean(KEY_SKIP_LOCATION_DIALOG, true);
                editor.apply();

            } else {
                startLocationWorker();
            }
        } else {
            startLocationWorker();
        }

    }

    private void startLocationWorker() {
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

    private void goToAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        appSettingsLauncher.launch(intent);
    }

    private boolean notificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    void sendFCMTokenToServer(String token){

        HttpClient httpClient = new HttpClient();

        Map<String, String> request = new HashMap<>();
        request.put("token", token);

        ResponseWrapper response =
                httpClient.sentToServerWithCookie("PUT","notifications/firebase/token", request );

        if(response.getStatusCode() == 200){
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putBoolean(KEY_FCM_SUBMIT, true);
            editor.apply();
        }

    }

    void getFCMTokenAndSave(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    // Log and toast
                    Log.d("FCM", token);
                    Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                });
    }


    public class WebAppInterface {
        Context context;

        WebAppInterface(Context c) {
            context = c;
        }

        // 자바스크립트에서 이 함수를 호출할 수 있게 @JavascriptInterface annotation 추가
        @JavascriptInterface
        public void showToast(String msg) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        String cookies = CookieManager.getInstance().getCookie(SpringServerUrl);
        Log.d("쿠키확인", "onStop: " + cookies);
        CookieManager.getInstance().flush();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.stopLoading();
            webView.setWebViewClient(null);
            webView.destroy();
            webView = null;
        }

        String cookies = CookieManager.getInstance().getCookie(SpringServerUrl);
        Log.d("쿠키확인", "onDestroy: " + cookies);

        CookieManager.getInstance().flush();
        Log.d("onDestroy", "WebView 및 쿠키 정리 완료");
    }
}
