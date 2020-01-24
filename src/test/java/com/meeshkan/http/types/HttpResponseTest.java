package com.meeshkan.http.types;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HttpResponseTest {

    @Test
    void equals() {
        HttpResponse response1 = new HttpResponse.Builder()
                .setBody("body")
                .setStatusCode(200)
                .build();
        HttpResponse response2 = new HttpResponse.Builder()
                .setBody("body")
                .setStatusCode(200)
                .build();
        HttpResponse response3 = new HttpResponse.Builder()
                .setBody("...")
                .setStatusCode(200)
                .build();
        HttpResponse response4 = new HttpResponse.Builder()
                .setBody("body")
                .setStatusCode(201)
                .build();

        Assertions.assertEquals(response1, response2);
        Assertions.assertNotEquals(response1, response3);
        Assertions.assertNotEquals(response1, response4);
        Assertions.assertNotEquals(response3, response4);
    }

}
