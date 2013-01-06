package org.danielge.doorbells.api;

import com.google.gson.Gson;

public class JsonResponse {
    protected int responseCode;
    protected String body;

    public JsonResponse(int responseCode, String body) {
        this.responseCode = responseCode;
        this.body = body;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getBody() {
        return body;
    }

    public <T> T fromJson(Class<T> jsonObjectClass) {
        Gson gson = new Gson();
        return gson.fromJson(this.body, jsonObjectClass);
    }
}
