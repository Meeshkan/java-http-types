package com.meeshkan.http.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * HTTP request or response headers.
 */
public class HttpHeaders {
    private final Map<String, List<String>> headerMap;

    private HttpHeaders(Map<String, List<String>> headerMap) {
        this.headerMap = Collections.unmodifiableMap(headerMap);
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
     * All header values for the given header name.
     *
     * @param headerName the header name
     * @return an immutable list of header values, or an empty list if none
     */
    @NotNull
    public List<String> getAll(@NotNull String headerName) {
        headerName = headerName.toLowerCase();
        List<String> list = headerMap.get(headerName);
        if (list == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * Header values as a map from header names to a list of header values.
     *
     * @return the HTTP headers as a map
     */
    public Map<String, List<String>> asMap() {
        return headerMap;
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

    @Override
    public String toString() {
        return "HttpHeaders{" + headerMap + '}';
    }

    /**
     * Builder of immutable {@link HttpHeaders} instances.
     */
    public static class Builder {
        private final Map<String, List<String>> headerMap = new HashMap<>();

        /**
         * Add the given, single header value under the given name.
         *
         * @param headerName  the header name
         * @param headerValue the header value
         * @return this builder
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
         * @return this builder
         */
        public HttpHeaders.Builder addAll(@NotNull String headerName, @NotNull Collection<String> headerValues) {
            headerName = headerName.toLowerCase();
            List<String> list = headerMap.computeIfAbsent(headerName, k -> new ArrayList<>());
            list.addAll(headerValues);
            return this;
        }

        /**
         * Create a HTTP headers instance using the headers set on this builder.
         *
         * @return the built instance
         */
        public HttpHeaders build() {
            return new HttpHeaders(this.headerMap);
        }

    }

}
