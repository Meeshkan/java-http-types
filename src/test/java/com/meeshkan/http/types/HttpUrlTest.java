package com.meeshkan.http.types;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class HttpUrlTest {

    @Test
    void asUrl() throws MalformedURLException {
        HttpUrl url = new HttpUrl.Builder()
                .protocol(HttpProtocol.HTTP)
                .host("example.com")
                .pathname("/my/path")
                .addQueryParameter("mykey", "myvalue")
                .build();

        Assertions.assertEquals(new URL("http://example.com/my/path?mykey=myvalue"), url.asUrl());
    }

    @Test
    void fromUrl() throws MalformedURLException {
        HttpUrl url = new HttpUrl.Builder()
                .protocol(HttpProtocol.HTTP)
                .url(new URL("https://example.com/my/path?mykey=myvalue&n=v1&n=v2"))
                .build();

        Assertions.assertEquals(HttpProtocol.HTTPS, url.getProtocol());
        Assertions.assertEquals("example.com", url.getHost());
        Assertions.assertEquals("/my/path", url.getPathname());
        Assertions.assertEquals("myvalue", url.getFirstQueryParameter("mykey"));
        Assertions.assertEquals("v1", url.getFirstQueryParameter("n"));
        Assertions.assertEquals(Arrays.asList("v1", "v2"), url.getAllQueryParameters("n"));
    }

}
