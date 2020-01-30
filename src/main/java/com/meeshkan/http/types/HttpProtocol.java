package com.meeshkan.http.types;

/**
 * HTTP protocol - either {@link #HTTP} or {@link #HTTPS}.
 */
public enum HttpProtocol {
    /**
     * The unencrypted HTTP protocol.
     */
    HTTP,

    /**
     * The encrypted HTTPS protocol.
     */
    HTTPS
}
