package com.meeshkan.http.types;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpRequestTest {

    @Test
    void httpRequest() {
        Instant timestamp = Instant.now().minus(10, ChronoUnit.MINUTES);
        HttpRequest request = new HttpRequest.Builder()
                .timestamp(timestamp)
                .headers(new HttpHeaders.Builder()
                        .add("header", "value")
                        .build())
                .method(HttpMethod.GET)
                .url(new HttpUrl.Builder()
                        .protocol(HttpProtocol.HTTP)
                        .host("example.com")
                        .pathname("/path")
                        .queryParameters(Collections.singletonMap("param", "value"))
                        .build())
                .body("body")
                .build();

        assertEquals(timestamp, request.getTimestamp());
        assertEquals(HttpMethod.GET, request.getMethod());
        assertEquals(HttpProtocol.HTTP, request.getUrl().getProtocol());
        assertEquals("example.com", request.getUrl().getHost());
        assertEquals("/path", request.getUrl().getPathname());
        assertEquals(Collections.singletonMap("param", Collections.singletonList("value")), request.getUrl().getQueryParameters());
        assertEquals("body", request.getBody());
    }
}
