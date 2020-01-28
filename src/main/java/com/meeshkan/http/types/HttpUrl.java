package com.meeshkan.http.types;

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
        this.queryParameters = queryParameters;
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
