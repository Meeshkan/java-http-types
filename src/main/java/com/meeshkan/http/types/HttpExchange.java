package com.meeshkan.http.types;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    public static HttpExchange fromJson(String in) throws IOException {
        return fromJson(new StringReader(in));
    }

    public static HttpExchange fromJson(InputStream in) throws IOException {
        return fromJson(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    public static HttpExchange fromJson(Reader reader) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader bufferedReader;
        if (reader instanceof BufferedReader) {
            bufferedReader = (BufferedReader) reader;
        } else {
            bufferedReader = new BufferedReader(reader);
        }

        String line;
        while ((line = bufferedReader.readLine()) != null) {
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
            urlBuilder.path(path);
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

    static Stream<HttpExchange> parseJsonl(String jsonl) throws IOException {
        return parseJsonl(new StringReader(jsonl));
    }

    static Stream<HttpExchange> parseJsonl(InputStream in) throws IOException {
        return parseJsonl(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    static Stream<HttpExchange> parseJsonl(Reader reader) throws IOException {
        BufferedReader bufferedReader;
        if (reader instanceof BufferedReader) {
            bufferedReader = (BufferedReader) reader;
        } else {
            bufferedReader = new BufferedReader(reader);
        }

        Iterator<HttpExchange> exchangeIterator = new Iterator<HttpExchange>() {
            private String nextLine;

            {
                nextLine = bufferedReader.readLine();
            }

            @Override
            public boolean hasNext() {
                return nextLine != null;
            }

            @Override
            public HttpExchange next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                try {
                    HttpExchange parsedExchange = HttpExchange.fromJson(nextLine);
                    nextLine = bufferedReader.readLine();
                    return parsedExchange;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Spliterator<HttpExchange> spliterator = Spliterators.spliteratorUnknownSize(exchangeIterator, Spliterator.NONNULL);
        return StreamSupport.stream(spliterator, false);
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
