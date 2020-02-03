package com.meeshkan.http.types;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class HttpResponseTest {

    @Test
    void httpResponse() {
        Instant timestamp = Instant.now().minus(10, ChronoUnit.MINUTES);
        HttpResponse response = new HttpResponse.Builder()
                .timestamp(timestamp)
                .headers(new HttpHeaders.Builder()
                        .add("header", "value")
                        .build())
                .body("body")
                .build();

        assertEquals(timestamp, response.getTimestamp());
        assertEquals("value", response.getHeaders().getFirst("header"));
        assertEquals(Collections.singletonList("value"), response.getHeaders().getAll("header"));
        assertEquals("body", response.getBody());
    }

    @Test
    void equals() {
        HttpResponse response1 = new HttpResponse.Builder()
                .body("body")
                .statusCode(200)
                .build();
        HttpResponse response2 = new HttpResponse.Builder()
                .body("body")
                .statusCode(200)
                .build();
        HttpResponse response3 = new HttpResponse.Builder()
                .body("...")
                .statusCode(200)
                .build();
        HttpResponse response4 = new HttpResponse.Builder()
                .body("body")
                .statusCode(201)
                .build();

        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertNotEquals(response1, response4);
        assertNotEquals(response3, response4);
    }

}
