package com.meeshkan.http.types;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpExchangeTest {

    @Test
    void basicUsage() {
        HttpExchange exchange = new HttpExchange.Builder()
                .request(new HttpRequest.Builder()
                        .headers(new HttpHeaders.Builder()
                                .add("RequestHeader", "value")
                                .build())
                        .method(HttpMethod.GET)
                        .url(new HttpUrl.Builder()
                                .protocol(HttpProtocol.HTTP)
                                .host("example.com")
                                .pathname("/path")
                                .queryParameters(Collections.singletonMap("param", "value"))
                                .build())
                        .body("requestBody")
                        .build())
                .response(new HttpResponse.Builder()
                        .headers(new HttpHeaders.Builder()
                                .add("ResponseHeader", "value")
                                .build())
                        .statusCode(200)
                        .body("responseBody")
                        .build())
                .build();

        assertEquals("requestBody", exchange.getRequest().getBody());
        assertEquals("value", exchange.getRequest().getHeaders().getFirst("RequestHeader"));
        assertEquals("responseBody", exchange.getResponse().getBody());
    }

    @Test
    void loadFromJson() throws Exception {
        HttpExchange exchange = HttpExchange.fromJson(getClass().getResourceAsStream("/sample-with-pathname-and-query.json"));

        assertEquals(HttpMethod.GET, exchange.getRequest().getMethod());

        assertEquals("/user/repos", exchange.getRequest().getUrl().getPathname());
        String path = exchange.getRequest().getUrl().getPath();
        assertTrue(path.startsWith("/user/repos?"));
        assertTrue(path.contains("mykey=myvalue"));
        assertTrue(path.contains("anotherkey=value1"));
        assertTrue(path.contains("anotherkey=value2"));
        assertEquals("myvalue", exchange.getRequest().getUrl().getFirstQueryParameter("mykey"));
        assertEquals(Collections.singletonList("myvalue"), exchange.getRequest().getUrl().getAllQueryParameters("mykey"));
        assertEquals("value1", exchange.getRequest().getUrl().getFirstQueryParameter("anotherkey"));
        assertEquals(Arrays.asList("value1", "value2"), exchange.getRequest().getUrl().getAllQueryParameters("anotherkey"));

        assertEquals("*/*", exchange.getRequest().getHeaders().getFirst("accept"));
        assertEquals("Mozilla/5.0 (pc-x86_64-linux-gnu) Siege/3.0.8", exchange.getRequest().getHeaders().getFirst("user-agent"));

        assertEquals(200, exchange.getResponse().getStatusCode());
        assertEquals("1999", exchange.getResponse().getHeaders().getFirst("content-length"));
    }

    @Test
    void loadFromJsonWithQueryParametersInsidePath() throws Exception {
        HttpExchange exchange = HttpExchange.fromJson(getClass().getResourceAsStream("/sample-with-path.json"));

        assertEquals(HttpMethod.GET, exchange.getRequest().getMethod());

        assertEquals("/user/repos", exchange.getRequest().getUrl().getPathname());
        String path = exchange.getRequest().getUrl().getPath();
        assertTrue(path.startsWith("/user/repos?"));
        assertTrue(path.contains("mykey=myvalue"));
        assertTrue(path.contains("anotherkey=value1"));
        assertTrue(path.contains("anotherkey=value2"));
        assertEquals("myvalue", exchange.getRequest().getUrl().getFirstQueryParameter("mykey"));
        assertEquals(Collections.singletonList("myvalue"), exchange.getRequest().getUrl().getAllQueryParameters("mykey"));
        assertEquals("value1", exchange.getRequest().getUrl().getFirstQueryParameter("anotherkey"));
        assertEquals(Arrays.asList("value1", "value2"), exchange.getRequest().getUrl().getAllQueryParameters("anotherkey"));

        assertEquals("*/*", exchange.getRequest().getHeaders().getFirst("accept"));
        assertEquals("Mozilla/5.0 (pc-x86_64-linux-gnu) Siege/3.0.8", exchange.getRequest().getHeaders().getFirst("user-agent"));

        assertEquals(200, exchange.getResponse().getStatusCode());
        assertEquals("1999", exchange.getResponse().getHeaders().getFirst("content-length"));
    }

    @Test
    void parseJsonl() throws Exception {
        List<HttpExchange> exchanges = HttpExchange.parseJsonl(getClass().getResourceAsStream("/sample.jsonl")).collect(Collectors.toList());
        assertEquals(2, exchanges.size());
        assertEquals("/user/repos1", exchanges.get(0).getRequest().getUrl().getPathname());
        assertEquals(HttpProtocol.HTTP, exchanges.get(0).getRequest().getUrl().getProtocol());
        assertEquals(HttpMethod.GET, exchanges.get(0).getRequest().getMethod());
        assertEquals("/user/repos2", exchanges.get(1).getRequest().getUrl().getPathname());
        assertEquals(HttpProtocol.HTTPS, exchanges.get(1).getRequest().getUrl().getProtocol());
        assertEquals(HttpMethod.POST, exchanges.get(1).getRequest().getMethod());
    }
}
