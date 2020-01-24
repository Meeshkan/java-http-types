package com.meeshkan.http.types;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpHeadersTest {

    @Test
    void httpHeaders() {
        HttpHeaders headers = new HttpHeaders.Builder()
                .add("header", "value")
                .add("header1", "value1")
                .add("header1", "value2")
                .addAll("headers", asList("v1", "v2"))
                .build();

        assertEquals("value", headers.getFirst("header"));
        assertEquals(singletonList("value"), headers.getAll("header"));
        assertEquals("value1", headers.getFirst("header1"));
        assertEquals(asList("value1", "value2"), headers.getAll("header1"));
        assertEquals("v1", headers.getFirst("headers"));
        assertEquals(asList("v1", "v2"), headers.getAll("headers"));

        HttpHeaders headers2 = new HttpHeaders.Builder()
                .add("header", "value")
                .add("header1", "value1")
                .add("header1", "value2")
                .addAll("headers", asList("v1", "v2"))
                .build();
        assertEquals(headers, headers2);
        assertEquals(headers.hashCode(), headers2.hashCode());
    }

}
