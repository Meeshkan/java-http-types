package com.meeshkan.http.types;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpExchangeTest {

    @Test
    void basicUsage() throws MalformedURLException {
        HttpExchange exchange = new HttpExchange.Builder()
                .setRequest(new HttpRequest.Builder()
                        .setHeaders(new HttpHeaders.Builder()
                                .add("RequestHeader", "value")
                                .build())
                        .setMethod(HttpMethod.GET)
                        .setUrl(new URL("http://example.com/path?param=value"))
                        .setBody("requestBody")
                        .build())
                .setResponse(new HttpResponse.Builder()
                        .setHeaders(new HttpHeaders.Builder()
                                .add("ResponseHeader", "value")
                                .build())
                        .setStatusCode(200)
                        .setBody("responseBody")
                        .build())
                .build();

        assertEquals("requestBody", exchange.getRequest().getBody());
        assertEquals("value", exchange.getRequest().getHeaders().getFirst("RequestHeader"));
        assertEquals("responseBody", exchange.getResponse().getBody());
    }

    @Test
    void loadFromJson() throws Exception {
        HttpExchange exchange = HttpExchange.fromJson(getClass().getResourceAsStream("/sample.json"));
        assertEquals(HttpMethod.GET, exchange.getRequest().getMethod());
        assertEquals("*/*", exchange.getRequest().getHeaders().getFirst("accept"));
        assertEquals("Mozilla/5.0 (pc-x86_64-linux-gnu) Siege/3.0.8", exchange.getRequest().getHeaders().getFirst("user-agent"));

        assertEquals(200, exchange.getResponse().getStatusCode());
        assertEquals("1999", exchange.getResponse().getHeaders().getFirst("content-length"));
    }

}
