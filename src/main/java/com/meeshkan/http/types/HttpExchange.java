package com.meeshkan.http.types;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

        HttpHeaders.Builder requestHeaders = new HttpHeaders.Builder();
        JSONObject requestHeadersObject = requestObject.getJSONObject("headers");
        for (String headerName : requestHeadersObject.keySet()) {
            String headerValue = requestHeadersObject.getString(headerName);
            requestHeaders.add(headerName, headerValue);
        }

        HttpRequest.Builder requestBuilder = new HttpRequest.Builder()
                .setMethod(methodEnum)
                .setHeaders(requestHeaders.build());

        HttpHeaders.Builder responseHeaders = new HttpHeaders.Builder();
        JSONObject responseHeadersObject = responseObject.getJSONObject("headers");
        for (String headerName : responseHeadersObject.keySet()) {
            String headerValue = responseHeadersObject.getString(headerName);
            responseHeaders.add(headerName, headerValue);
        }

        HttpResponse.Builder responseBuilder = new HttpResponse.Builder()
                .setStatusCode(responseObject.getInt("statusCode"))
                .setHeaders(responseHeaders.build())
                .setBody(responseObject.getString("body"));

        return new HttpExchange.Builder()
                .setRequest(requestBuilder.build())
                .setResponse(responseBuilder.build())
                .build();
    }

    public static class Builder {

        private HttpRequest request;
        private HttpResponse response;

        public HttpExchange.Builder setRequest(HttpRequest request) {
            this.request = request;
            return this;
        }

        public HttpExchange.Builder setResponse(HttpResponse response) {
            this.response = response;
            return this;
        }

        public HttpExchange build() {
            return new HttpExchange(request, response);
        }
    }

}
