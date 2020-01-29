package com.meeshkan.http.types;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpMethodTest {

    @Test
    void allMethods() {
        // A silly test to silence IntelliJ warnings about unused declarations.
        assertEquals("DELETE", HttpMethod.DELETE.name());
        assertEquals("PUT", HttpMethod.PUT.name());
        assertEquals("PATCH", HttpMethod.PATCH.name());
        assertEquals("OPTIONS", HttpMethod.OPTIONS.name());
        assertEquals("TRACE", HttpMethod.TRACE.name());
        assertEquals("HEAD", HttpMethod.HEAD.name());
        assertEquals("CONNECT", HttpMethod.CONNECT.name());
    }

}
