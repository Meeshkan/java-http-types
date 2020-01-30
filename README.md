# java-http-types
[![Build Status](https://github.com/Meeshkan/java-http-types/workflows/Java%20CI/badge.svg)](https://github.com/Meeshkan/java-http-types/actions?query=workflow%3A%22Java+CI%22)
[![MIT licensed](http://img.shields.io/:license-MIT-blue.svg)](LICENSE)
[![Package on Maven Central](https://img.shields.io/maven-central/v/com.meeshkan/http-types)](https://search.maven.org/artifact/com.meeshkan/http-types/)
[![javadoc](https://www.javadoc.io/badge/com.meeshkan/http-types.svg)](https://www.javadoc.io/doc/com.meeshkan/http-types)

Java library to read and write records of HTTP exchanges in the [HTTP types](https://meeshkan.github.io/http-types/) format.

Requires Java 8 or later.

# Reading HTTP exchanges from JSON Lines
```java
InputStream input = getClass().getResourceAsStream("/sample.jsonl");
HttpExchangeReader.fromJsonLines(input)
    .filter(exchange -> exchange.getResponse().getStatusCode() == 200)
    .forEach(exchange -> {
        HttpRequest request = exchange.getRequest();
        HttpUrl url = request.getUrl();
        HttpRequest response = exchange.getRequest();
        System.out.println("A " + request.getMethod() + " request to " +
            url.getHost() + " with response body " + response.getBody());
});
```

# Creating an HTTP exchange manually and writing it
```java
try (HttpExchangeWriter writer = new HttpExchangeWriter(new FileOutputStream("output.jsonl"))) {
    HttpExchange exchange =
        new HttpExchange.Builder()
            .request(new HttpRequest.Builder()
            .headers(new HttpHeaders.Builder()
                .add("header1", "value1")
                .add("header2", "value2")
                .build())
            .method(HttpMethod.GET)
            .url(new HttpUrl.Builder()
                .protocol(HttpProtocol.HTTP)
                .host("example.com")
                .pathname("/path")
                .queryParameters(Collections.singletonMap("param", "value"))
                .build())
            .body("requestBody")
            .build())
        .response(new HttpResponse.Builder()
            .headers(new HttpHeaders.Builder()
                .add("header", "value")
                .build())
            .statusCode(200)
            .body("responseBody")
            .build())
        .build();

    writer.write(exchange);
}
```
