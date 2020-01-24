package com.meeshkan.http.types;

/**
 * A HTTP request and response pair.
 */
public final class HttpExchange {

    private final HttpRequest request;
    private final HttpResponse response;

    public HttpRequest getRequest() {
        return request;
    }

    public HttpResponse getResponse() {
        return response;
    }

    private HttpExchange(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
    }

    public static class Builder {

        private HttpRequest request;
        private HttpResponse response;

        public HttpExchange.Builder setRequest(HttpRequest request) {
            this.request = request;
            return this;
        }

        public HttpExchange.Builder setResponse(HttpResponse response) {
            this.response = response;
            return this;
        }

        public HttpExchange build() {
            return new HttpExchange(request, response);
        }
    }

}
