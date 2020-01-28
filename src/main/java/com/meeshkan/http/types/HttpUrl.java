package com.meeshkan.http.types;

import java.io.UnsupportedEncodingException;
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

        public HttpUrl build() {
            return new HttpUrl(protocol, host, pathname, queryParameters);
        }
    }
}
