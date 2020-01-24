package com.meeshkan.http.types;

import java.net.URL;
import java.util.Objects;

public final class HttpRequest {

    private final URL url;
    private final HttpMethod method;
    private final HttpHeaders headers;
    private final String body;

    public HttpMethod getMethod() {
        return method;
    }

    public HttpProtocol getProtocol() {
        return "http".equals(url.getProtocol()) ? HttpProtocol.HTTP : HttpProtocol.HTTPS;
    }

    public String getBody() {
        return body;
    }

    public URL getUrl() {
        return url;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public HttpRequest(URL url, HttpMethod method, HttpHeaders headers, String body) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;
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

        private URL url;
        private HttpMethod method;
        private HttpHeaders headers;
        private String body;

        public HttpRequest.Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public HttpRequest.Builder setUrl(URL url) {
            this.url = url;
            return this;
        }

        public HttpRequest.Builder setHeaders(HttpHeaders headers) {
            this.headers = headers;
            return this;
        }

        public HttpRequest.Builder setMethod(HttpMethod method) {
            this.method = method;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(url, method, headers, body);
        }
    }

}
