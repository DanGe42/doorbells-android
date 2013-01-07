package org.danielge.doorbells.api;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

class ApiUtils {
    public static final String API_URL = "http://localhost:3000/api";     // FIXME: USE HTTPS

    public static URL endpoint (String resource) throws MalformedURLException {
        return new URL(API_URL + resource);
    }

    static class Params {
        private StringBuilder result = new StringBuilder("");
        private Params() {}

        public Params addParam (String field, String value) {
            if (result.length() > 0) {
                result.append('&');
            }

            result.append(field);
            result.append('=');
            try {
                result.append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return this;
        }

        public Params addParam (String field, int value) {
            return addParam(field, String.valueOf(value));
        }

        public static Params start() {
            return new Params();
        }

        public static Params start (String field, String value) {
            return Params.start().addParam(field, value);
        }

        public static Params start (String field, int value) {
            return Params.start().addParam(field, value);
        }

        public boolean isEmpty() {
            return result.length() == 0;
        }

        public String finish() {
            return this.toString();
        }

        @Override
        public String toString() {
            return result.toString();
        }
    }

    public static String concatQuery (String resource, Params query) {
        String res;
        if (query == null || query.isEmpty()) {
            res = resource;
        } else {
            res = resource + '?' + query.finish();
        }

        return res;
    }
}
