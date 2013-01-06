package org.danielge.doorbells.api;

import org.danielge.doorbells.utils.Utils;

import java.io.*;
import java.net.HttpURLConnection;

import static org.danielge.doorbells.api.ApiUtils.Params;

class PostJsonResponse extends JsonResponse{
    private int responseCode;
    private String body;

    private PostJsonResponse(int responseCode, String body) {
        super(responseCode, body);
    }

    public static PostJsonResponse makeRequest(String resource, String contents)
            throws IOException {
        contents = (contents == null) ? "" : contents;

        HttpURLConnection connection =
                (HttpURLConnection) ApiUtils.endpoint(resource).openConnection();

        try {
            connection.setDoOutput(true); // sets this to make a POST request
            connection.setRequestProperty("Accept", Utils.MIME_JSON);
            connection.setRequestProperty("Content-Type", Utils.MIME_FORM);
            connection.setRequestProperty("Content-Length", String.valueOf(contents.length()));

            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            out.write(contents.getBytes());
            out.close();

            InputStream in = new BufferedInputStream(connection.getInputStream());
            String responseBody = Utils.inputStreamToString(in);
            in.close();

            return new PostJsonResponse(connection.getResponseCode(), responseBody);
        } finally {
            connection.disconnect();
        }
    }

    public static PostJsonResponse makeRequest(String resource,
                                               Params urlParams,
                                               String contents) throws IOException {
        return makeRequest(ApiUtils.concatQuery(resource, urlParams), contents);
    }

    public static PostJsonResponse makeRequest(String resource,
                                               String token,
                                               Params urlParams,
                                               String contents) throws IOException {
        if (urlParams == null) {
            Params params = Params.start("auth_token", token);
            return makeRequest(resource, params, contents);
        } else {
            urlParams.addParam("auth_token", token);
            return makeRequest(resource, urlParams, contents);
        }
    }
}