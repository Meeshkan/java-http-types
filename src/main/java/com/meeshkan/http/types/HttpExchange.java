package com.meeshkan.http.types;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A immutable HTTP request and response pair.
 * <p>
 * To work with serialization in the <a href="https://meeshkan.github.io/http-types/">HTTP Types</a> format, use use {@link HttpExchangeReader} and {@link HttpExchangeWriter}.
 * <p>
 * Use {@link Builder} to create instances manually.
 */
public final class HttpExchange {
    @NotNull
    private final HttpRequest request;
    @NotNull
    private final HttpResponse response;

    private HttpExchange(@NotNull HttpRequest request, @NotNull HttpResponse response) {
        this.request = request;
        this.response = response;
    }

    /**
     * The HTTP request made in this exchange, which caused the HTTP response returned by {@link #getResponse()}.
     *
     * @return the request part of this exchange
     */
    @NotNull
    public HttpRequest getRequest() {
        return request;
    }

    /**
     * The HTTP response made in this exchange, caused by the HTTP request returned by {@link #getRequest()}.
     *
     * @return the response part of this exchange
     */
    @NotNull
    public HttpResponse getResponse() {
        return response;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpExchange that = (HttpExchange) o;
        return request.equals(that.request) &&
                response.equals(that.response);
    }

    @Override
    public int hashCode() {
        return Objects.hash(request, response);
    }

    @Override
    public String toString() {
        return "HttpExchange{" +
                "request=" + request +
                ", response=" + response +
                '}';
    }

    /**
     * Builder of {@link HttpExchange}.
     */
    public static class Builder {
        private HttpRequest request;
        private HttpResponse response;

        /**
         * Set the HTTP request on this exchange.
         *
         * @param request the request to set on the builder
         * @return this builder
         * @see #getRequest()
         */
        public HttpExchange.Builder request(HttpRequest request) {
            this.request = request;
            return this;
        }

        /**
         * Set the HTTP response on this exchange.
         *
         * @param response the request to set on the builder
         * @return this builder
         * @see #getResponse()
         */
        public HttpExchange.Builder response(HttpResponse response) {
            this.response = response;
            return this;
        }

        /**
         * Create a HTTP exchange using the request and response set on this builder.
         *
         * @return the built instance
         */
        public HttpExchange build() {
            Assert.assertNotNull("request", request);
            Assert.assertNotNull("response", response);
            return new HttpExchange(request, response);
        }

    }

}
