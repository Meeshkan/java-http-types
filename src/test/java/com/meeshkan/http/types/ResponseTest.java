package com.meeshkan.http.types;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ResponseTest {


    @Test
    void equals() {
        Response response1 = new Response.Builder()
                .setBody("body")
                .setStatusCode(200)
                .build();
        Response response2 = new Response.Builder()
                .setBody("body")
                .setStatusCode(200)
                .build();
        Assertions.assertEquals(response1, response2);
    }
}
