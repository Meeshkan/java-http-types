package com.meeshkan.http.types;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * A HTTP request and response pair.
 */
public final class HttpExchange {
    private final HttpRequest request;
    private final HttpResponse response;

    public HttpRequest getRequest() {
        return request;
    }

    public HttpResponse getResponse() {
        return response;
    }

    private HttpExchange(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
    }

    public static HttpExchange fromJson(InputStream in) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append('\n');
        }

        JSONObject jsonObject = new JSONObject(buffer.toString());
        JSONObject requestObject = jsonObject.getJSONObject("request");
        JSONObject responseObject = jsonObject.getJSONObject("response");

        String methodString = requestObject.getString("method");
        HttpMethod methodEnum = HttpMethod.valueOf(methodString.toUpperCase());

        String protocolString = requestObject.getString("protocol");
        HttpProtocol protocolEnum = HttpProtocol.valueOf(protocolString.toUpperCase());

        HttpRequest.Builder requestBuilder = new HttpRequest.Builder()
                .method(methodEnum);

        HttpUrl.Builder urlBuilder = new HttpUrl.Builder();
        urlBuilder.protocol(protocolEnum);
        try {
            String pathnameString = requestObject.getString("pathname");
            try {
                JSONObject queryObject = requestObject.getJSONObject("query");
                for (String queryParameter : queryObject.keySet()) {
                    try {
                        String queryValue = queryObject.getString(queryParameter);
                        urlBuilder.addQueryParameter(queryParameter, queryValue);
                    } catch (JSONException e) {
                        // Should be an array.
                        JSONArray queryArray = queryObject.getJSONArray(queryParameter);
                        for (Object queryValue : queryArray) {
                            urlBuilder.addQueryParameter(queryParameter, (String) queryValue);
                        }
                    }
                }
            } catch (JSONException e) {
                // Ignore, query not mandatory.
            }
            urlBuilder.pathname(pathnameString);
        } catch (JSONException e1) {
            String path = requestObject.getString("path");
            URL asUrl = new URL("file:" + path);
            urlBuilder.pathname(asUrl.getPath());
            final String[] pairs = asUrl.getQuery().split("&");
            for (String pair : pairs) {
                final int idx = pair.indexOf("=");
                final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "utf-8") : pair;
                final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "utf-8") : null;
                urlBuilder.addQueryParameter(key, value);
            }
        }
        requestBuilder.url(urlBuilder.build());

        HttpHeaders.Builder requestHeaders = new HttpHeaders.Builder();
        JSONObject requestHeadersObject = requestObject.getJSONObject("headers");
        for (String headerName : requestHeadersObject.keySet()) {
            String headerValue = requestHeadersObject.getString(headerName);
            requestHeaders.add(headerName, headerValue);
        }
        requestBuilder.headers(requestHeaders.build());

        HttpHeaders.Builder responseHeaders = new HttpHeaders.Builder();
        JSONObject responseHeadersObject = responseObject.getJSONObject("headers");
        for (String headerName : responseHeadersObject.keySet()) {
            String headerValue = responseHeadersObject.getString(headerName);
            responseHeaders.add(headerName, headerValue);
        }

        HttpResponse.Builder responseBuilder = new HttpResponse.Builder()
                .statusCode(responseObject.getInt("statusCode"))
                .headers(responseHeaders.build())
                .body(responseObject.getString("body"));

        return new HttpExchange.Builder()
                .request(requestBuilder.build())
                .response(responseBuilder.build())
                .build();
    }

    public static class Builder {

        private HttpRequest request;
        private HttpResponse response;

        public HttpExchange.Builder request(HttpRequest request) {
            this.request = request;
            return this;
        }

        public HttpExchange.Builder response(HttpResponse response) {
            this.response = response;
            return this;
        }

        public HttpExchange build() {
            return new HttpExchange(request, response);
        }
    }

}
