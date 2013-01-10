package org.danielge.doorbells.api;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

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

    public <T> T fromJson(Class<T> jsonObjectClass) throws InternalServerException {
        Gson gson = new Gson();
        try {
            return gson.fromJson(this.body, jsonObjectClass);
        } catch (JsonSyntaxException e) {
            throw new InternalServerException("server did not return JSON");
        }
    }
}
