package com.meeshkan.http.types;

class Assert {

    static void assertNotNull(String fieldName, Object object) {
        if (object == null) {
            throw new NullPointerException("'" + fieldName + "' cannot be null");
        }
    }

}
