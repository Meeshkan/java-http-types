package com.meeshkan.http.types;

import java.util.Objects;

public class Response {

    private final String body;
    private final int statusCode;

    Response(String body, int statusCode) {
        this.body = body;
        this.statusCode = statusCode;
    }

    public String getBody() {
        return body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response response = (Response) o;
        return statusCode == response.statusCode &&
                body.equals(response.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body, statusCode);
    }

    public static class Builder {

        private String body;
        private int statusCode;

        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public Builder setStatusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Response build() {
            return new Response(body, statusCode);
        }
    }
}
