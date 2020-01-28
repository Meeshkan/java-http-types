package com.meeshkan.http.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A set of HTTP request or response headers.
 */
public class HttpHeaders {

    private final Map<String, List<String>> headerMap;

    private HttpHeaders(Map<String, List<String>> headerMap) {
        this.headerMap = headerMap;
    }

    /**
     * Return the first header value for the given header name, if any.
     *
     * @param headerName the header name
     * @return the first header value, or null if none
     */
    @Nullable
    public String getFirst(@NotNull String headerName) {
        headerName = headerName.toLowerCase();
        List<String> list = headerMap.get(headerName);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * Return all header values for the given header name, if any.
     *
     * @param headerName the header name
     * @return an immutable list of header values, or null if none
     */
    @Nullable
    public List<String> getAll(@NotNull String headerName) {
        headerName = headerName.toLowerCase();
        List<String> list = headerMap.get(headerName);
        if (list == null) {
            return null;
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpHeaders that = (HttpHeaders) o;
        return headerMap.equals(that.headerMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headerMap);
    }

    public static class Builder {

        private final Map<String, List<String>> headerMap = new HashMap<>();

        /**
         * Add the given, single header value under the given name.
         *
         * @param headerName  the header name
         * @param headerValue the header value
         */
        public HttpHeaders.Builder add(@NotNull String headerName, @NotNull String headerValue) {
            headerName = headerName.toLowerCase();
            List<String> list = headerMap.computeIfAbsent(headerName, k -> new ArrayList<>());
            list.add(headerValue);
            return this;
        }

        /**
         * Add a collection of header values under the given name.
         *
         * @param headerName   the header name
         * @param headerValues the header values
         */
        public HttpHeaders.Builder addAll(@NotNull  String headerName, @NotNull Collection<String> headerValues) {
            headerName = headerName.toLowerCase();
            List<String> list = headerMap.computeIfAbsent(headerName, k -> new ArrayList<>());
            list.addAll(headerValues);
            return this;
        }

        public HttpHeaders build() {
            return new HttpHeaders(this.headerMap);
        }
    }

}
