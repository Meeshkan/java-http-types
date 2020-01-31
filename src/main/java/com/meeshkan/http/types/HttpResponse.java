package com.meeshkan.http.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;

/**
 * HTTP response.
 *
 * @see HttpExchange#getResponse()
 */
public final class HttpResponse {
    @Nullable
    private final String body;
    private final int statusCode;
    @NotNull
    private final HttpHeaders headers;
    @Nullable
    private final Instant timestamp;

    /**
     * Time at which the HTTP response was sent.
     *
     * @return the HTTP response sent time, or null if none
     */
    @Nullable
    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatusCode() {
        return statusCode;
    }

    /**
     * HTTP headers used in this response.
     *
     * @return the HTTP headers used in this response
     */
    @NotNull
    public HttpHeaders getHeaders() {
        return headers;
    }

    /**
     * Body string of this HTTP response.
     *
     * @return the body string of this response, or null if none
     */
    @Nullable
    public String getBody() {
        return body;
    }

    private HttpResponse(@Nullable String body, int statusCode, @NotNull HttpHeaders headers, @Nullable Instant timestamp) {
        this.body = body;
        this.statusCode = statusCode;
        this.headers = headers;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "body='" + body + '\'' +
                ", statusCode=" + statusCode +
                ", headers=" + headers +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpResponse that = (HttpResponse) o;
        return statusCode == that.statusCode &&
                Objects.equals(body, that.body) &&
                headers.equals(that.headers) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body, statusCode, headers, timestamp);
    }

    /**
     * Builder of immutable {@link HttpResponse} instances.
     */
    public static class Builder {
        private String body;
        private int statusCode;
        private HttpHeaders headers;
        private Instant timestamp;

        /**
         * Set the optional response body on the HTTP response to build.
         *
         * @param body response body to set
         * @return this builder
         * @see #getBody()
         */
        public Builder body(String body) {
            this.body = body;
            return this;
        }

        /**
         * Set the status code on the HTTP response to build.
         *
         * @param statusCode response status code to set
         * @return this builder
         * @see #getStatusCode()
         */
        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        /**
         * Set the HTTP headers on the HTTP response to build.
         *
         * @param headers response headers to set
         * @return this builder
         * @see #getHeaders()
         */
        public Builder headers(HttpHeaders headers) {
            this.headers = headers;
            return this;
        }

        /**
         * Set the optional HTTP sent time on the HTTP response to build.
         *
         * @param timestamp the response sent time to set
         * @return this builder
         * @see #getTimestamp()
         */
        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        /**
         * Create a HTTP response using the properties set on this builder
         *
         * @return the built instance
         */
        public HttpResponse build() {
            return new HttpResponse(body, statusCode, headers == null ? new HttpHeaders.Builder().build() : headers, timestamp);
        }

    }

}
