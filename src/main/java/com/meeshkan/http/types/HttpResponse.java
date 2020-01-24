package com.meeshkan.http.types;

import java.util.Objects;

public final class HttpResponse {

    private final String body;
    private final int statusCode;
    private final HttpHeaders headers;

    public String getBody() {
        return body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    HttpResponse(String body, int statusCode, HttpHeaders headers) {
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

    public static class Builder {

        private String body;
        private int statusCode;
        private HttpHeaders headers;

        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public Builder setStatusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder setHeaders(HttpHeaders headers) {
            this.headers = headers;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(body, statusCode, headers);
        }
    }
}
