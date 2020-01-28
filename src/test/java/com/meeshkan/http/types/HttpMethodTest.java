package com.meeshkan.http.types;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HttpMethodTest {

    @Test
    void allMethods() {
        // A silly test to silence IntelliJ warnings about unused declarations.
        Assertions.assertEquals("DELETE", HttpMethod.DELETE.name());
        Assertions.assertEquals("PUT", HttpMethod.PUT.name());
        Assertions.assertEquals("PATCH", HttpMethod.PATCH.name());
        Assertions.assertEquals("OPTIONS", HttpMethod.OPTIONS.name());
        Assertions.assertEquals("TRACE", HttpMethod.TRACE.name());
        Assertions.assertEquals("HEAD", HttpMethod.HEAD.name());
        Assertions.assertEquals("CONNECT", HttpMethod.CONNECT.name());
    }

}
