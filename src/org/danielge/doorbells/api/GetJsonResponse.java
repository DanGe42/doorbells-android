package org.danielge.doorbells.api;

import org.danielge.doorbells.utils.Utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import static org.danielge.doorbells.api.ApiUtils.Params;

class GetJsonResponse extends JsonResponse {
    public GetJsonResponse(int responseCode, String body) {
        super(responseCode, body);
    }

    public static GetJsonResponse makeRequest (String resource)
            throws IOException {
        HttpURLConnection connection =
                (HttpURLConnection) ApiUtils.endpoint(resource).openConnection();

        try {
            connection.setRequestProperty("Accept", Utils.MIME_JSON);

            InputStream in = new BufferedInputStream(connection.getInputStream());
            String responseBody = Utils.inputStreamToString(in);
            in.close();

            return new GetJsonResponse(connection.getResponseCode(), responseBody);
        } finally {
            connection.disconnect();
        }
    }

    public static GetJsonResponse makeRequest (String resource,
                                               Params urlParams) throws IOException {
        return makeRequest(ApiUtils.concatQuery(resource, urlParams));
    }

    public static GetJsonResponse makeRequest (String resource,
                                               String token,
                                               Params urlParams) throws IOException {

        if (urlParams == null) {
            Params params = Params.start("auth_token", token);
            return makeRequest(resource, params);
        } else {
            urlParams.addParam("auth_token", token);
            return makeRequest(resource, urlParams);
        }
    }
}
