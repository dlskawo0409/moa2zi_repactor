package com.ssafy.moa2zi.common.http;

import org.json.JSONObject;

public class ResponseWrapper {
    private Integer statusCode;
    private JSONObject body;

    public ResponseWrapper(int statusCode, JSONObject body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public JSONObject getBody() {
        return body;
    }
}
