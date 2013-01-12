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

    public String getAuthToken() {
        return authToken;
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

    /**
     * Initialize the client using the provided authentication token. It is assumed
     * that the programmer is initializing the client with an auth token from a
     * previous session. If the token is invalid, all client calls will likely result
     * in an {@code UnauthorizedException} being thrown.
     *
     * @param authToken The authentication token
     * @return  An instantiated {@code DoorbellsClient} object.
     */
    public static DoorbellsClient initialize (String authToken) {
        return new DoorbellsClient(authToken);
    }

    public boolean invalidate() throws IOException {
        JsonResponse response =
                PostJsonResponse.makeAuthRequest("/auth/destroy", authToken, null, null);

        return response.getResponseCode() == HTTP_OK;
    }


    /* API for GCM registration */

    /**
     * Registers a device via the registration ID provided by the Google Cloud
     * Messaging API. This method does not return anything.
     *
     * @param regId The GCM-assigned device registration ID
     *
     * @throws InternalServerException  Could be thrown if the device registration
     *                                  server is down
     * @throws UnauthorizedException    If authorization failed.
     * @throws DoorbellsApiException    If something is wrong with the server.
     * @throws IOException  If the client fails to connect to the server
     */
    public void registerDevice (String regId)
            throws IOException, DoorbellsApiException {
        Params params = Params.start("id", regId);
        JsonResponse response = PostJsonResponse.makeAuthRequest("/register",
                authToken, params, null);

        if (response.getResponseCode() == HTTP_OK) {
            return;
        }

        defaultErrorHandler(response);
    }

    /**
     * Unregisters a device via the registration ID provided by the Google Cloud
     * Messaging API.
     *
     * @param regId The GCM-assigned device registration ID
     * @return  {@code true} if the server succeeded in unregistering the device.
     *          {@code false} if the server could not find the device ID
     * @throws UnauthorizedException    If authorization failed.
     * @throws DoorbellsApiException    If a server error occurs.
     * @throws IOException  If the client fails to connect to the server
     */
    public boolean unregisterDevice (String regId)
            throws IOException, DoorbellsApiException {
        Params params = Params.start("id", regId);
        JsonResponse response = PostJsonResponse.makeAuthRequest("/unregister",
                authToken, params, null);

        switch (response.getResponseCode()) {
            case HTTP_OK:
                return true;
            case HTTP_NOT_FOUND:
                return false;
            default:
                defaultErrorHandler(response);
                return false;   // satisfy the compiler
        }
    }


    /* API for messages */

    /**
     * Gets a message with the specified ID.
     *
     * @param id    The integer ID of the message
     * @return  The Message object returned from the server, or {@code null} if a message
     *          with the specified ID does not exist.
     * @throws IOException  If the client fails to connect to the server
     * @throws DoorbellsApiException    If a server error occurs
     */
    public Message getMessage (int id) throws IOException, DoorbellsApiException {
        JsonResponse response = GetJsonResponse.makeAuthRequest(
                "/messages/" + id, authToken, null);

        switch (response.getResponseCode()) {
            case HTTP_OK:
                return response.fromJson(Message.class);
            case HTTP_NOT_FOUND:
                return null;
            default:
                defaultErrorHandler(response);
                return null;    // satisfy the compiler
        }
    }

    /**
     * Returns a list of Messages stored on the server for the specified user.
     * Currently, this returns 15 messages.
     *
     * @return  An array of Messages
     * @throws IOException  If the client fails to connect to the server
     * @throws DoorbellsApiException    If a server error occurs
     */
    public Message[] getMessages() throws IOException, DoorbellsApiException {
        JsonResponse response = GetJsonResponse.makeAuthRequest(
                "/messages", authToken, null);

        if (response.getResponseCode() == HTTP_OK) {
            return response.fromJson(GetMessagesResponse.class).messages;
        }

        defaultErrorHandler(response);
        return null;    // satisfy compiler
    }

    private static class GetMessagesResponse {
        private int status;
        private Message[] messages;

        GetMessagesResponse() {}
    }


    public boolean sendMessage (String tagId, String message)
            throws IOException, DoorbellsApiException {
        Params urlParams = Params.start("tag", tagId);
        String contents = Params.start("contents", message).finish();
        JsonResponse response = PostJsonResponse.makeAuthRequest(
                "/send", authToken, urlParams, contents);

        switch (response.getResponseCode()) {
            case HTTP_OK:
                return true;
            case HTTP_NOT_FOUND:
                return false;
            default:
                defaultErrorHandler(response);
                return false;   // satisfy compiler
        }

    }

    public boolean deleteMessage (int msgId)
            throws IOException, DoorbellsApiException {
        Params params = Params.start("id", msgId);
        JsonResponse response = PostJsonResponse.makeAuthRequest(
                "/messages/delete", authToken, params, null);

        switch (response.getResponseCode()) {
            case HTTP_OK:
                return true;
            case HTTP_NOT_FOUND:
                return false;
            default:
                defaultErrorHandler(response);
                return false;   // satisfy compiler
        }
    }


    /* API for tags */

    public Tag getTag (String tagId) throws IOException, DoorbellsApiException {
        JsonResponse response = GetJsonResponse.makeAuthRequest(
                "/tags/" + tagId, authToken, null);

        switch (response.getResponseCode()) {
            case HTTP_OK:
                return response.fromJson(Tag.class);
            case HTTP_NOT_FOUND:
                return null;
            default:
                defaultErrorHandler(response);
                return null;
        }
    }

    public Tag[] getTags() throws IOException, DoorbellsApiException {
        JsonResponse response = GetJsonResponse.makeAuthRequest(
                "/tags", authToken, null);


        if (response.getResponseCode() == HTTP_OK) {
            return response.fromJson(GetTagsResponse.class).tags;
        }

        defaultErrorHandler(response);
        return null;
    }

    private static class GetTagsResponse {
        private int status;
        private Tag[] tags;

        GetTagsResponse() {}
    }

    public Tag createTag (String location) throws IOException, DoorbellsApiException {
        String contents = Params.start("location", location).finish();
        JsonResponse response = PostJsonResponse.makeAuthRequest(
                "/tags/create", authToken, null, contents);

        if (response.getResponseCode() == 200) {
            return response.fromJson(Tag.class);
        }

        defaultErrorHandler(response);
        return null;
    }

    public boolean updateTag (String tagId, String newLocation)
            throws IOException, DoorbellsApiException {
        String contents = Params.start("location", newLocation).finish();
        Params urlParams = Params.start("id", tagId);
        JsonResponse response = PostJsonResponse.makeAuthRequest(
                "/tags/update", authToken, urlParams, contents);

        switch (response.getResponseCode()) {
            case HTTP_OK:
                return true;
            case HTTP_NOT_FOUND:
                return false;
            default:
                defaultErrorHandler(response);
                return false;
        }
    }

    public boolean destroyTag (String tagId) throws IOException, DoorbellsApiException {
        Params params = Params.start("id", tagId);
        JsonResponse response = PostJsonResponse.makeAuthRequest(
                "/tags/delete", authToken, params, null);

        switch (response.getResponseCode()) {
            case HTTP_OK:
                return true;
            case HTTP_NOT_FOUND:
                return false;
            default:
                defaultErrorHandler(response);
                return false;
        }
    }

    private void defaultErrorHandler (JsonResponse response)
            throws DoorbellsApiException {
        JsonStatusResponse status = response.fromJson(JsonStatusResponse.class);

        switch (status.status) {
            case HTTP_BAD_REQUEST:
                throw new BadRequestException(status.msg);
            case HTTP_UNAUTHORIZED:
                throw new UnauthorizedException(status.msg);
            case HTTP_SERVER_ERROR:
                throw new InternalServerException(status.msg);
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
