package com.ssafy.moa2zi.common.http;

import android.util.Log;
import android.webkit.CookieManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpClient {

    private final String SpringServerUrl = "https://j12a403.p.ssafy.io";
    private final String SpringApiUrl = SpringServerUrl + "/api/v1/";

    public ResponseWrapper sentToServerWithCookie(
            String method,
            String detailUrl,
            Map<?, ?> jsonAppendMap) {
        try {
            URL url = new URL(SpringApiUrl + detailUrl);

            Log.d("URL", url.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            CookieManager cookieManager = CookieManager.getInstance();
            String cookies = cookieManager.getCookie(SpringServerUrl);

            if (cookies == null) {
                return new ResponseWrapper(401, null);
            }

            conn.setRequestMethod(method);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Cookie", cookies);
            Log.d("Cookie", "서버에 보낼 쿠키: " + cookies);

            JSONObject json = new JSONObject();
            for (Map.Entry<?, ?> entry : jsonAppendMap.entrySet()) {
                json.put(String.valueOf(entry.getKey()), entry.getValue());
            }

            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();

            Integer responseCode = conn.getResponseCode();
            Log.d("PostResponseCode", "서버 응답 코드: " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();


            cookieManager.flush();

            String result = response.toString();
            if (result.isEmpty()) {
                return new ResponseWrapper(responseCode, null);
            } else {
                return new ResponseWrapper(responseCode, new JSONObject(result));
            }

        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }


}
