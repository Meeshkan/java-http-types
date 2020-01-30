package com.meeshkan.http.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * HTTP request.
 *
 * @see HttpExchange#getRequest()
 */
public final class HttpRequest {
    @NotNull
    private final HttpUrl url;
    @NotNull
    private final HttpMethod method;
    @NotNull
    private final HttpHeaders headers;
    @Nullable
    private final String body;

    /**
     * URL describing the request target of this HTTP request.
     *
     * @return the URL of this request
     */
    @NotNull
    public HttpUrl getUrl() {
        return url;
    }

    /**
     * HTTP method indicating the desired action of this request.
     *
     * @return the HTTP method used in this request
     */
    @NotNull
    public HttpMethod getMethod() {
        return method;
    }

    /**
     * HTTP headers used in this request.
     *
     * @return the HTTP headers used in this request
     */
    @NotNull
    public HttpHeaders getHeaders() {
        return headers;
    }

    /**
     * Body string of this HTTP request, if any.
     *
     * @return the body string of this request, or null if none
     */
    @Nullable
    public String getBody() {
        return body;
    }

    public HttpRequest(@NotNull HttpUrl url, @NotNull HttpMethod method, @NotNull HttpHeaders headers, @Nullable String body) {
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

    /**
     * Builder of {@link HttpResponse}.
     */
    public static class Builder {
        private HttpUrl url;
        private HttpMethod method;
        private HttpHeaders headers;
        private String body;

        /**
         * Set the body string part of the HTTP request to build.
         *
         * @param body the body string part of a HTTP request
         * @return this builder
         * @see #getBody()
         */
        public HttpRequest.Builder body(String body) {
            this.body = body;
            return this;
        }

        /**
         * Set the URL part of the HTTP request to build.
         *
         * @param url the URL part of a HTTP request
         * @return this builder
         * @see #getUrl()
         */
        public HttpRequest.Builder url(HttpUrl url) {
            this.url = url;
            return this;
        }

        /**
         * Set the headers of the HTTP request to build.
         *
         * @param headers the headers of a HTTP request
         * @return this builder
         * @see #getHeaders()
         */
        public HttpRequest.Builder headers(HttpHeaders headers) {
            this.headers = headers;
            return this;
        }

        /**
         * Set the HTTP method on the HTTP request.
         *
         * @param method the HTTP method to set
         * @return this builder
         * @see #getMethod()
         */
        public HttpRequest.Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        /**
         * Create a HTTP request using the properties set on this builder
         *
         * @return the built instance
         */
        public HttpRequest build() {
            Assert.assertNotNull("url", url);
            Assert.assertNotNull("method", method);
            return new HttpRequest(url, method, headers == null ? new HttpHeaders.Builder().build() : headers, body);
        }

    }

}
