package org.danielge.doorbells.api;

import java.io.IOException;

import static org.danielge.doorbells.api.ApiUtils.Params;

public class DoorbellsClient {
    public static final String TAG = DoorbellsClient.class.getSimpleName();

    private static final int HTTP_OK = 200,
                             HTTP_BAD_REQUEST = 400,
                             HTTP_UNAUTHORIZED = 401,
                             HTTP_NOT_FOUND = 404,
                             HTTP_SERVER_ERROR = 500;

    private String authToken;   // Guaranteed never to be null

    private DoorbellsClient (String authToken) {
        this.authToken = authToken;
    }

    private static class AuthData {
        private String token;

        AuthData() {}
    }

    private static class JsonStatusResponse {
        protected int status;
        protected String msg;

        JsonStatusResponse() {}
    }


    /* API for authorization */

    /**
     * Attempts to authenticate and authorize the user with the given email and
     * password. If authorization fails (i.e. 400 or 401 from the server),
     * an {@code UnauthorizedException} is thrown. If authorization succeeds,
     * a new {@code DoorbellsClient} object is returned.
     *
     * @param email     The user's email
     * @param password  The user's password
     * @return  An instantiated {@code DoorbellsClient} object on success
     *
     * @throws UnauthorizedException    if authorization fails
     * @throws DoorbellsApiException    if something else goes wrong. This should be
     *                                  handled as a server error
     * @throws IOException  if the client fails to connect to the server,
     *                      for whatever reason
     */
    public static DoorbellsClient authorize (String email, String password)
            throws DoorbellsApiException, IOException {
        String params = Params.start("email", email)
                              .addParam("password", password)
                              .finish();
        JsonResponse response = PostJsonResponse.makeRequest("/auth", params);

        if (response.getResponseCode() == HTTP_OK) {
            AuthData auth = response.fromJson(AuthData.class);
            return new DoorbellsClient(auth.token);
        }

        JsonStatusResponse status = response.fromJson(JsonStatusResponse.class);
        switch (status.status) {
            case HTTP_BAD_REQUEST:
                throw new UnauthorizedException(status.msg);
            case HTTP_UNAUTHORIZED:
                throw new UnauthorizedException(status.msg);
            default:
                throw new DoorbellsApiException(status.msg);
        }
    }

    public boolean invalidate() throws IOException {
        JsonResponse response =
                PostJsonResponse.makeAuthRequest("/auth/destroy", authToken, null, null);

        return response.getResponseCode() == HTTP_OK;
    }


    /* API for GCM registration */

    public boolean registerDevice (String regId) throws IOException {
        Params params = Params.start("id", regId);
        JsonResponse response = PostJsonResponse.makeAuthRequest("/register",
                authToken, params, null);

        return response.getResponseCode() == 200;
    }

    public boolean unregisterDevice (String regId) throws IOException {
        Params params = Params.start("id", regId);
        JsonResponse response = PostJsonResponse.makeAuthRequest("/unregister",
                authToken, params, null);

        return response.getResponseCode() == 200;

    }


    /* API for messages */

    public Message getMessage (int id) throws IOException {
        JsonResponse response = GetJsonResponse.makeAuthRequest(
                "/messages/" + id, authToken, null);

        if (response.getResponseCode() == 200) {
            return response.fromJson(Message.class);
        }

        return null;
    }

    public Message[] getMessages() throws IOException {
        JsonResponse response = GetJsonResponse.makeAuthRequest(
                "/messages", authToken, null);

        if (response.getResponseCode() == 200) {
            return response.fromJson(GetMessagesResponse.class).messages;
        }

        return null;
    }

    private static class GetMessagesResponse {
        private int status;
        private Message[] messages;

        GetMessagesResponse() {}
    }

    public boolean sendMessage (String tagId, String message) throws IOException {
        Params urlParams = Params.start("tag", tagId);
        String contents = Params.start("contents", message).finish();
        JsonResponse response = PostJsonResponse.makeAuthRequest(
                "/send", authToken, urlParams, contents);

        return response.getResponseCode() == 200;

    }

    public boolean deleteMessage (int msgId) throws IOException {
        Params params = Params.start("id", msgId);
        JsonResponse response = PostJsonResponse.makeAuthRequest(
                "/messages/delete", authToken, params, null);

        return response.getResponseCode() == 200;
    }


    /* API for tags */

    public Tag getTag (String tagId) throws IOException, DoorbellsApiException {
        JsonResponse response = GetJsonResponse.makeAuthRequest(
                "/tags/" + tagId, authToken, null);

        if (response.getResponseCode() == 200) {
            return response.fromJson(Tag.class);
        }

        JsonStatusResponse status = response.fromJson(JsonStatusResponse.class);
        switch (response.getResponseCode()) {
            case 404:
                throw new ResourceNotFoundException(status.msg);
            default:
                defaultErrorHandler(status);
        }

        return null;
    }

    public Tag[] getTags() throws IOException, DoorbellsApiException {
        JsonResponse response = GetJsonResponse.makeAuthRequest(
                "/tags", authToken, null);

        if (response.getResponseCode() == 200) {
            return response.fromJson(GetTagsResponse.class).tags;
        }

        JsonStatusResponse status = response.fromJson(JsonStatusResponse.class);
        defaultErrorHandler(status);

        return null;
    }

    private static class GetTagsResponse {
        private int status;
        private Tag[] tags;

        GetTagsResponse() {}
    }

    public Tag createTag (String location) throws IOException {
        String contents = Params.start("location", location).finish();
        JsonResponse response = PostJsonResponse.makeAuthRequest(
                "/tags/create", authToken, null, contents);

        if (response.getResponseCode() == 200) {
            return response.fromJson(Tag.class);
        }

        return null;
    }

    public boolean updateTag (String tagId, String newLocation) throws IOException {
        String contents = Params.start("location", newLocation).finish();
        Params urlParams = Params.start("id", tagId);
        JsonResponse response = PostJsonResponse.makeAuthRequest(
                "/tags/update", authToken, urlParams, contents);

        return response.getResponseCode() == 200;
    }

    public boolean destroyTag (String tagId) throws IOException {
        Params params = Params.start("id", tagId);
        JsonResponse response = PostJsonResponse.makeAuthRequest(
                "/tags/delete", authToken, params, null);

        return response.getResponseCode() == 200;
    }

    private void defaultErrorHandler (JsonStatusResponse status)
            throws DoorbellsApiException {
        switch (status.status) {
            case 401:
                throw new UnauthorizedException(status.msg);
            default:
                throw new DoorbellsApiException(status.msg);
        }
    }

    public static void main (String[] args) throws IOException, DoorbellsApiException {
        DoorbellsClient client = DoorbellsClient.authorize("daniel@example.com", "password");
        Message[] messages = client.getMessages();
        for (int i = 0; i < messages.length; i++) {
            System.out.println("message[" + i + "] = " + messages[i].getContents());
            System.out.println("timestamp=" + messages[i].getDateReceived());
        }
        System.out.println(client.getMessage(3).getContents());
        client.invalidate();
    }
}
