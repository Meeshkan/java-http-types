package com.meeshkan.http.types;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpUrlTest {

    @Test
    void asUrl() throws MalformedURLException {
        HttpUrl url = new HttpUrl.Builder()
                .protocol(HttpProtocol.HTTP)
                .host("example.com")
                .pathname("/my/path")
                .addQueryParameter("mykey", "myvalue")
                .build();

        assertEquals(new URL("http://example.com/my/path?mykey=myvalue"), url.asUrl());
    }

    @Test
    void fromUrl() throws MalformedURLException {
        HttpUrl url = new HttpUrl.Builder()
                .protocol(HttpProtocol.HTTP)
                .url(new URL("https://example.com/my/path?mykey=myvalue&n=v1&n=v2"))
                .build();

        assertEquals(HttpProtocol.HTTPS, url.getProtocol());
        assertEquals("example.com", url.getHost());
        assertEquals("/my/path", url.getPathname());
        assertEquals("myvalue", url.getFirstQueryParameter("mykey"));
        assertEquals("v1", url.getFirstQueryParameter("n"));
        assertEquals(Arrays.asList("v1", "v2"), url.getAllQueryParameters("n"));
    }

    @Test
    void queryParametersMultivalued() {
        Map<String, List<String>> queryParameters = new HashMap<>();
        queryParameters.put("mykey", Collections.singletonList("myvalue"));
        queryParameters.put("n", Arrays.asList("v1", "v2"));
        HttpUrl url = new HttpUrl.Builder()
                .protocol(HttpProtocol.HTTP)
                .host("example.com")
                .pathname("/my/path")
                .queryParametersMultivalued(queryParameters)
                .build();

        assertEquals("myvalue", url.getFirstQueryParameter("mykey"));
        assertEquals("v1", url.getFirstQueryParameter("n"));
        assertEquals(Arrays.asList("v1", "v2"), url.getAllQueryParameters("n"));
    }

}
