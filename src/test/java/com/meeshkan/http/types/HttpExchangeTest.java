package com.meeshkan.http.types;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpExchangeTest {

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

}
