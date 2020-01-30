package com.meeshkan.http.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * HTTP request URL.
 * <p>
 * Represent the same information as the {@link URL} class in the standard java library, but limited to HTTP(S) and with convenience methods to access query parameters.
 * </p>
 *
 * @see HttpRequest#getUrl()
 * @see #asUrl()
 */
public class HttpUrl {
    @NotNull
    private final HttpProtocol protocol;
    @NotNull
    private final String host;
    @NotNull
    private final String pathname;
    @NotNull
    private final Map<String, List<String>> queryParameters;

    public HttpUrl(@NotNull HttpProtocol protocol, @NotNull String host, @NotNull String pathname, @NotNull Map<String, List<String>> queryParameters) {
        this.protocol = protocol;
        this.host = host;
        this.pathname = pathname;
        this.queryParameters = Collections.unmodifiableMap(queryParameters);
    }

    /**
     * HTTP protocol of this URL.
     *
     * @return the protocol of this URL
     */
    @NotNull
    public HttpProtocol getProtocol() {
        return protocol;
    }

    /**
     * Host of this URL, including and optional port number.
     * <p>
     * Example values:
     * <ul>
     *     <li>example.com</li>
     *     <li>example.com:80</li>
     * </ul>
     *
     * @return host part of this URL
     */
    @NotNull
    public String getHost() {
        return host;
    }

    /**
     * The path of this URL, not including query.
     * <p>
     * Use {@link #getPath()} to get the path and the URL-encoded query together.
     * </p>
     * Example: For the URL http://example.com/a/path?q=v, this method will return "/a/path".
     *
     * @return the path name part of this URL
     */
    @NotNull
    public String getPathname() {
        return pathname;
    }

    /**
     * The path of this URL, not including query.
     * <p>
     * Use {@link #getPathname()}} to get the path without any query parameters.
     * </p>
     * Example: For the URL http://example.com/a/path?q=v, this method will return "/a/path?q=v".
     *
     * @return the path and query part of this URL
     */
    @NotNull
    public String getPath() {
        StringBuilder result = new StringBuilder();
        result.append(pathname);
        if (!queryParameters.isEmpty()) {
            result.append('?');
            try {
                for (Map.Entry<String, List<String>> entry : queryParameters.entrySet()) {
                    String encodedName = URLEncoder.encode(entry.getKey(), "utf-8");
                    for (String value : entry.getValue()) {
                        String encodedValue = URLEncoder.encode(value, "utf-8");
                        result.append(encodedName).append('=').append(encodedValue);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                // Should never happen, "utf-8" is always a supported charset.
                // When targeting java 10 one can use URLEncoder.encode(s, StandardCharsets.UTF_8) which do not throw.
                throw new RuntimeException(e);
            }
        }
        return result.toString();
    }

    /**
     * All query parameters of this URL as a map.
     * <p>
     * Individual query parameters can be obtained with {@link #getFirstQueryParameter(String)} and {@link #getAllQueryParameters(String)}.
     *
     * @return the query parameters as a map
     */
    @NotNull
    public Map<String, List<String>> getQueryParameters() {
        return queryParameters;
    }

    /**
     * First query parameter value of the specified parameter name, or null if none.
     * <p>
     * Use {@link #getAllQueryParameters(String)} to get all values for this parameter.
     * </p>
     *
     * @param parameterName the query parameter name
     * @return the first query parameter value, or null if none
     */
    @Nullable
    public String getFirstQueryParameter(String parameterName) {
        List<String> currentList = queryParameters.get(parameterName);
        if (currentList == null) {
            return null;
        }
        return currentList.get(0);
    }

    /**
     * All query parameter values of the specified name, or an empty list if none.
     *
     * @param parameterName the query parameter name
     * @return all query parameter values
     * @see #getFirstQueryParameter(String)
     */
    @NotNull
    public List<String> getAllQueryParameters(String parameterName) {
        List<String> values = queryParameters.get(parameterName);
        return values == null ? Collections.emptyList() : Collections.unmodifiableList(values);
    }

    /**
     * This HTTP URL as a {@link URL} instance.
     *
     * @return this HTTP URL
     * @see Builder#url(URL)
     */
    @NotNull
    public URL asUrl() {
        try {
            return new URL(protocol.name().toLowerCase(), host, getPath());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpUrl httpUrl = (HttpUrl) o;
        return protocol == httpUrl.protocol &&
                host.equals(httpUrl.host) &&
                pathname.equals(httpUrl.pathname) &&
                Objects.equals(queryParameters, httpUrl.queryParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(protocol, host, pathname, queryParameters);
    }

    @Override
    public String toString() {
        return "HttpUrl{" +
                "protocol=" + protocol +
                ", host='" + host + '\'' +
                ", pathname='" + pathname + '\'' +
                ", queryParameters=" + queryParameters +
                '}';
    }

    /**
     * {@link HttpUrl} builder.
     */
    public static class Builder {
        private HttpProtocol protocol;
        private String host;
        private String pathname;
        private Map<String, List<String>> queryParameters;

        /**
         * Set the protocol part of the URL to build.
         *
         * @param protocol the protocol part of an URL
         * @return this builder:
         * @see #getProtocol()
         */
        public Builder protocol(HttpProtocol protocol) {
            this.protocol = protocol;
            return this;
        }

        /**
         * Set the host part of the URL to build.
         *
         * @param host host part of an URL
         * @return this builder
         * @see #getHost()
         */
        public Builder host(String host) {
            this.host = host;
            return this;
        }

        /**
         * Set the path name without any query parameters.
         * <p>
         * Use {@link #path(String)} to set the path name and query parameters simultaneously from a string with both the path name and query parameters, such as "/a/path?q=v".
         *
         * @param pathname the path name part of an URL
         * @return this builder
         * @see #getPathname()
         */
        public Builder pathname(String pathname) {
            if (pathname.contains("?")) {
                throw new IllegalArgumentException("The 'pathname' property should not contain '?' - use 'path' instead");
            }
            this.pathname = pathname;
            return this;
        }

        /**
         * Set the path name and query parameters.
         * <p>
         * Use {@link #pathname(String)} to only set the path name of the URL without overriding query parameters.
         * <p>
         * Note that
         *
         * <pre>
         *   builder.path("/a/path?q=v");
         * </pre>
         * <p>
         * is equivalent to:
         *
         * <pre>
         *   builder.pathname("/a/path").addQueryParameter("q", "v");
         * </pre>
         *
         * @param path the path name and query part of an url
         * @return this builder
         * @see #getPath()
         */
        public Builder path(String path) {
            this.queryParameters = new HashMap<>();
            try {
                URL asUrl = new URL("file:" + path);
                pathname(asUrl.getPath());
                if (asUrl.getQuery() != null) {
                    final String[] pairs = asUrl.getQuery().split("&");
                    for (String pair : pairs) {
                        final int idx = pair.indexOf("=");
                        final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "utf-8") : pair;
                        final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "utf-8") : null;
                        addQueryParameter(key, value);
                    }
                }
                return this;
            } catch (MalformedURLException | UnsupportedEncodingException e) {
                // FIXME: Avoid hack.
                throw new RuntimeException(e);
            }
        }

        /**
         * Set all query parameters from a single-valued map.
         * <p>
         * Use {@link #queryParametersMultivalued(Map)} for a version where parameters may have multiple values, or use {@link #addQueryParameter(String, String)} to add individual query parameters
         *
         * @param queryParameters the single-valued query parameters of an URL
         * @return this builder
         */
        public Builder queryParameters(Map<String, String> queryParameters) {
            this.queryParameters = new HashMap<>();
            for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
                this.queryParameters.put(entry.getKey(), Collections.singletonList(entry.getValue()));
            }
            return this;
        }

        /**
         * Set all query parameters from a multi-valued map.
         * <p>
         * Use {@link #queryParameters(Map)} for a convenience version if all parameters have a single value, or use {@link #addQueryParameter(String, String)} to add individual query parameters.
         *
         * @param queryParameters the multi-valued query parameters of an URL
         * @return this builder
         */
        public Builder queryParametersMultivalued(Map<String, List<String>> queryParameters) {
            this.queryParameters = new HashMap<>(queryParameters);
            return this;
        }

        /**
         * Add a single query parameter.
         * <p>
         * If the query parameter has already been added, this will create a multi-valued parameter. So to represent the query parameters of the URL "/path?q=v1&q=v2" the following calls can be made:
         * <pre>
         *   builder.addQueryParameter("q", "v1");
         *   builder.addQueryParameter("q", "v2");
         * </pre>
         * <p>
         * Use {@link #queryParameters(Map)} or {@link #queryParametersMultivalued(Map)} to set multiple query parameters from a map.
         *
         * @param name  the query parameter name to add
         * @param value the query parameter value to add
         * @return this builder
         */
        public Builder addQueryParameter(String name, String value) {
            if (queryParameters == null) {
                queryParameters = new HashMap<>();
            }
            List<String> currentList = queryParameters.computeIfAbsent(name, k -> new ArrayList<>());
            currentList.add(value);
            return this;
        }

        /**
         * Set all fields on this HTTP url from a {@link URL} instance
         *
         * @param url the URL instance to copy
         * @return this builder
         * @see #asUrl()
         */
        public Builder url(URL url) {
            if (!("http".equals(url.getProtocol()) || "https".equals(url.getProtocol()))) {
                throw new IllegalArgumentException("Invalid protocol (only 'http' and 'https' supported): " + url.getProtocol());
            }

            String path = url.getPath();
            if (url.getQuery() != null) {
                path += "?" + url.getQuery();
            }
            return this.protocol(HttpProtocol.valueOf(url.getProtocol().toUpperCase()))
                    .host(url.getHost())
                    .path(path);
        }

        /**
         * Create a HTTP URL using the properties set on this builder.
         *
         * @return the built instance
         */
        public HttpUrl build() {
            return new HttpUrl(protocol, host, pathname, queryParameters == null ? Collections.emptyMap() : queryParameters);
        }

    }

}
