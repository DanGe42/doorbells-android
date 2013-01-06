package org.danielge.doorbells.api;

import java.io.IOException;

import static org.danielge.doorbells.api.ApiUtils.Params;

public class DoorbellsClient {
    public static final String TAG = DoorbellsClient.class.getSimpleName();

    private String authToken;

    private DoorbellsClient (String authToken) {
        this.authToken = authToken;
    }

    private static class AuthData {
        private String token;

        AuthData() {}
    }

    private static class JsonStatusResponse {
        private int status;
        private String msg;

        JsonStatusResponse() {}
    }


    /* API for authorization */

    public static DoorbellsClient authorize (String email, String password)
            throws DoorbellsApiException, IOException {
        String params = Params.start("email", email)
                              .addParam("password", password)
                              .finish();
        JsonResponse response = PostJsonResponse.makeRequest("/auth", params);

        switch (response.getResponseCode()) {
            case 400:   // Bad request
                throw new UnauthorizedException();
            case 401:   // Unauthorized
                throw new UnauthorizedException();
            case 200:
                AuthData auth = response.fromJson(AuthData.class);
                return new DoorbellsClient(auth.token);
            default:
                throw new DoorbellsApiException();
        }
    }

    public boolean invalidate() throws IOException {
        String action = String.format("/auth/destroy?auth_token=%s", authToken);
        JsonResponse response =
                PostJsonResponse.makeRequest("/auth/destroy", authToken, null, null);

        if (response.getResponseCode() == 200) {
            return true;
        }

        return false;
    }


    /* API for GCM registration */

    public boolean registerDevice (String regId) throws IOException {
        Params params = Params.start("id", regId);
        JsonResponse response = PostJsonResponse.makeRequest("/register",
                authToken, params, null);

        if (response.getResponseCode() == 200) {
            return true;
        }

        return false;
    }

    public boolean unregisterDevice (String regId) throws IOException {
        Params params = Params.start("id", regId);
        JsonResponse response = PostJsonResponse.makeRequest("/unregister",
                authToken, params, null);

        if (response.getResponseCode() == 200) {
            return true;
        }

        return false;
    }


    /* API for messages */

    public Message getMessage (int id) throws IOException {
        JsonResponse response = GetJsonResponse.makeRequest("/messages/" + id, authToken, null);

        if (response.getResponseCode() == 200) {
            return response.fromJson(Message.class);
        }

        return null;
    }

    public Message[] getMessages() throws IOException {
        JsonResponse response = GetJsonResponse.makeRequest("/messages", authToken, null);

        if (response.getResponseCode() == 200) {
            return response.fromJson(GetMessagesResponse.class).messages;
        }

        return null;
    }

    private static class GetMessagesResponse {
        private int status;
        private Message[] messages;
    }

    public static void main (String[] args) throws IOException, DoorbellsApiException {
        DoorbellsClient client = DoorbellsClient.authorize("daniel@example.com", "password");
        client.invalidate();
    }
}
