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
 * A reader of {@link HttpExchange HTTP exchanges} in the <a href="https://meeshkan.github.io/http-types/">HTTP Types JSON Lines format</a>.
 *
 * @see HttpExchangeWriter
 */
public final class HttpExchangeReader {

    private HttpExchangeReader() {
        // Not for instantiation, only a container of static methods.
    }

    /**
     * Read a single HTTP exchange from a JSON formatted string in the HTTP types format.
     *
     * @param in the JSON representation of a single HTTP exchange
     * @return the parsed HTTP exchange
     * @see #fromJson(InputStream)
     * @see #fromJson(Reader)
     */
    public static HttpExchange fromJson(String in) {
        try {
            return fromJson(new StringReader(in));
        } catch (IOException e) {
            // Should not happen.
            throw new RuntimeException(e);
        }
    }

    /**
     * Read a single HTTP exchange from a JSON formatted string in the HTTP types format.
     *
     * @param in the JSON representation of a single HTTP exchange
     * @return the parsed HTTP exchange
     * @throws IOException if there is an I/O problem reading the input
     * @see #fromJson(String)
     * @see #fromJson(Reader)
     */
    public static HttpExchange fromJson(InputStream in) throws IOException {
        return fromJson(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    /**
     * Read a single HTTP exchange from a JSON formatted string in the HTTP types format.
     *
     * @param in the JSON representation of a single HTTP exchange
     * @return the parsed HTTP exchange
     * @throws IOException if there is an I/O problem reading the input
     * @see #fromJson(String)
     * @see #fromJson(InputStream)
     */
    public static HttpExchange fromJson(Reader in) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader bufferedReader;
        if (in instanceof BufferedReader) {
            bufferedReader = (BufferedReader) in;
        } else {
            bufferedReader = new BufferedReader(in);
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
        urlBuilder.host(requestObject.getString("host"));
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
            try {
                String headerValue = requestHeadersObject.getString(headerName);
                requestHeaders.add(headerName, headerValue);
            } catch (JSONException e) {
                // Might be an array.
                JSONArray headersArray = requestHeadersObject.getJSONArray(headerName);
                for (Object headerValue : headersArray) {
                    requestHeaders.add(headerName, (String) headerValue);
                }
            }
        }
        requestBuilder.headers(requestHeaders.build());

        HttpHeaders.Builder responseHeaders = new HttpHeaders.Builder();
        JSONObject responseHeadersObject = responseObject.getJSONObject("headers");
        for (String headerName : responseHeadersObject.keySet()) {
            try {
                String headerValue = responseHeadersObject.getString(headerName);
                responseHeaders.add(headerName, headerValue);
            } catch (JSONException e) {
                // Might be an array.
                JSONArray headersArray = responseHeadersObject.getJSONArray(headerName);
                for (Object headerValue : headersArray) {
                    responseHeaders.add(headerName, (String) headerValue);
                }
            }
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

    /**
     * Parse a stream of HTTP exchanges from a HTTP Types JSON Lines formatted input.
     *
     * @param jsonLines the input in HTTP TYPES JSON Lines format
     * @return a stream of HTTP Exchanges defined by this archive
     * @see #fromJsonLines(Reader)
     * @see #fromJsonLines(InputStream)
     */
    public static Stream<HttpExchange> fromJsonLines(String jsonLines) {
        try {
            return fromJsonLines(new StringReader(jsonLines));
        } catch (IOException e) {
            // Should not happen.
            throw new RuntimeException(e);
        }
    }

    /**
     * Parse a stream of HTTP exchanges from a HTTP Types JSON Lines formatted input.
     *
     * @param in the input in HTTP TYPES JSON Lines format
     * @return a stream of HTTP Exchanges defined by this archive
     * @throws IOException if there is a I/O problem reading the input
     * @see #fromJsonLines(Reader)
     * @see #fromJsonLines(String)
     */
    public static Stream<HttpExchange> fromJsonLines(InputStream in) throws IOException {
        return fromJsonLines(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    /**
     * Parse a stream of HTTP exchanges from a HTTP Types JSON Lines formatted input.
     *
     * @param reader the input in HTTP Types JSON Lines format
     * @return a stream of HTTP Exchanges defined by this archive
     * @throws IOException if there is an I/O problem reading the input.
     * @see #fromJsonLines(InputStream)
     * @see #fromJsonLines(String)
     */
    public static Stream<HttpExchange> fromJsonLines(Reader reader) throws IOException {
        BufferedReader bufferedReader;
        if (reader instanceof BufferedReader) {
            bufferedReader = (BufferedReader) reader;
        } else {
            bufferedReader = new BufferedReader(reader);
        }

        Iterator<HttpExchange> exchangeIterator = new Iterator<HttpExchange>() {
            private String nextLine = bufferedReader.readLine();

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
                    HttpExchange parsedExchange = HttpExchangeReader.fromJson(nextLine);
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

}
