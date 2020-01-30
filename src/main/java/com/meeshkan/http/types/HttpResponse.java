package com.meeshkan.http.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public String getBody() {
        return body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @NotNull
    public HttpHeaders getHeaders() {
        return headers;
    }

    private HttpResponse(@Nullable String body, int statusCode, @NotNull HttpHeaders headers) {
        this.body = body;
        this.statusCode = statusCode;
        this.headers = headers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpResponse that = (HttpResponse) o;
        return statusCode == that.statusCode &&
                Objects.equals(body, that.body) &&
                Objects.equals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body, statusCode, headers);
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "body='" + body + '\'' +
                ", statusCode=" + statusCode +
                ", headers=" + headers +
                '}';
    }

    /**
     * Builder of immutable {@link HttpResponse} instances.
     */
    public static class Builder {
        private String body;
        private int statusCode;
        private HttpHeaders headers;

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder headers(HttpHeaders headers) {
            this.headers = headers;
            return this;
        }

        /**
         * Create a HTTP response using the properties set on this builder
         *
         * @return the built instance
         */
        public HttpResponse build() {
            return new HttpResponse(body, statusCode, headers == null ? new HttpHeaders.Builder().build() : headers);
        }

    }

}
