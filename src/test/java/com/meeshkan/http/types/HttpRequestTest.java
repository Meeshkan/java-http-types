package com.meeshkan.http.types;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpRequestTest {

    @Test
    void httpRequest() throws MalformedURLException {
        HttpRequest request = new HttpRequest.Builder()
                .setHeaders(new HttpHeaders.Builder()
                        .add("header", "value")
                        .build())
                .setMethod(HttpMethod.GET)
                .setUrl(new URL("http://example.com/path?param=value"))
                .setBody("body")
                .build();

        assertEquals(HttpMethod.GET, request.getMethod());
        assertEquals(HttpProtocol.HTTP, request.getProtocol());
        assertEquals("body", request.getBody());
    }
}
