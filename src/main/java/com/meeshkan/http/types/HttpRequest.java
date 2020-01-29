package com.meeshkan.http.types;

import java.util.Objects;

public final class HttpRequest {
    private final HttpUrl url;
    private final HttpMethod method;
    private final HttpHeaders headers;
    private final String body;

    public HttpUrl getUrl() {
        return url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public HttpRequest(HttpUrl url, HttpMethod method, HttpHeaders headers, String body) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "url=" + url +
                ", method=" + method +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpRequest that = (HttpRequest) o;
        return url.equals(that.url) &&
                method == that.method &&
                headers.equals(that.headers) &&
                Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, method, headers, body);
    }

    public static class Builder {
        private HttpUrl url;
        private HttpMethod method;
        private HttpHeaders headers;
        private String body;

        public HttpRequest.Builder body(String body) {
            this.body = body;
            return this;
        }

        public HttpRequest.Builder url(HttpUrl url) {
            this.url = url;
            return this;
        }

        public HttpRequest.Builder headers(HttpHeaders headers) {
            this.headers = headers;
            return this;
        }

        public HttpRequest.Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(url, method, headers, body);
        }
    }

}
