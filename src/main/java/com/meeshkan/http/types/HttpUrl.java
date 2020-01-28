package com.meeshkan.http.types;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

public class HttpUrl {
    private final HttpProtocol protocol;
    private final String host;
    private final String pathname;
    private final Map<String, List<String>> queryParameters;

    public HttpUrl(HttpProtocol protocol, String host, String pathname, Map<String, List<String>> queryParameters) {
        this.protocol = protocol;
        this.host = host;
        this.pathname = pathname;
        this.queryParameters = queryParameters == null ? Collections.emptyMap() : queryParameters;
    }

    public HttpProtocol getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String getPathname() {
        return pathname;
    }

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

    public Map<String, List<String>> getQueryParameters() {
        return queryParameters;
    }

    public String getFirstQueryParameter(String parameterName) {
        List<String> currentList = queryParameters.get(parameterName);
        if (currentList == null) {
            return null;
        }
        return currentList.get(0);
    }

    public List<String> getAllQueryParameters(String parameterName) {
        return queryParameters.get(parameterName);
    }

    public URL asUrl() {
        try {
            return new URL(protocol.name().toLowerCase(), host, getPath());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Builder {
        private HttpProtocol protocol;
        private String host;
        private String pathname;
        private Map<String, List<String>> queryParameters;

        public Builder protocol(HttpProtocol protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder pathname(String pathname) {
            this.pathname = pathname;
            return this;
        }

        public Builder path(String path) {
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

        public Builder queryParameters(Map<String, String> queryParameters) {
            this.queryParameters = new HashMap<>();
            for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
                this.queryParameters.put(entry.getKey(), Collections.singletonList(entry.getValue()));
            }
            return this;
        }

        public Builder queryParametersMultivalued(Map<String, List<String>> queryParameters) {
            this.queryParameters = new HashMap<>(queryParameters);
            return this;
        }

        public Builder addQueryParameter(String key, String value) {
            if (queryParameters == null) {
                queryParameters = new HashMap<>();
            }
            List<String> currentList = queryParameters.computeIfAbsent(key, k -> new ArrayList<>());
            currentList.add(value);
            return this;
        }

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

        public HttpUrl build() {
            return new HttpUrl(protocol, host, pathname, queryParameters);
        }
    }
}
