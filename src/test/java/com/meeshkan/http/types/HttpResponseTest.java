package com.meeshkan.http.types;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class HttpResponseTest {

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
