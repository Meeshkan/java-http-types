# http-types in Java
[![Build Status](https://github.com/Meeshkan/java-http-types/workflows/Java%20CI/badge.svg)](https://github.com/Meeshkan/java-http-types/actions?query=workflow%3A%22Java+CI%22)
[![MIT licensed](http://img.shields.io/:license-MIT-blue.svg)](LICENSE)
[![Package on Maven Central](https://img.shields.io/maven-central/v/com.meeshkan/http-types)](https://search.maven.org/artifact/com.meeshkan/http-types/)
[![javadoc](https://www.javadoc.io/badge/com.meeshkan/http-types.svg)](https://www.javadoc.io/doc/com.meeshkan/http-types)

Java (8 or later) library to read and write records of HTTP exchanges in the [http-types](https://meeshkan.github.io/http-types/) format.

# Using this library
Releases are available on [Maven Central](https://search.maven.org/artifact/com.meeshkan/http-types/0.4.0/jar).

```gradle
dependencies {
    implementation 'com.meeshkan:http-types:0.5.0'
}
```

# Writing HTTP exchanges
Using [HttpExchangeWriter](https://www.javadoc.io/static/com.meeshkan/http-types/0.4.0/com/meeshkan/http/types/HttpExchangeWriter.html) a recording of HTTP traffic can be serialised for use with any program that can handle the HTTP Types format.
```java
try (var writer = new HttpExchangeWriter(new FileOutputStream("output.jsonl"))) {
    HttpRequest request = new HttpRequest.Builder()
        .method(HttpMethod.GET)
        .url(new HttpUrl.Builder()
            .protocol(HttpProtocol.HTTP)
            .host("example.com")
            .pathname("/path")
            .queryParameters(Collections.singletonMap("param", "value"))
            .build())    
        .headers(new HttpHeaders.Builder()
            .add("header1", "value1")
            .add("header2", "value2")
            .build())
        .body("requestBody")
        .build();

    HttpResponse = new HttpResponse.Builder()
        .statusCode(200)
        .headers(new HttpHeaders.Builder()
            .add("header", "value")
            .build())
        .body("responseBody")
        .build());

    HttpExchange exchange = new HttpExchange.Builder()
            .request(request)
            .response(response)
            .build();

    writer.write(exchange);
    
    // Normally you would write multiple exchanges in the same recording.
}
```

# Reading HTTP exchanges
With [HttpExchangeReader](https://www.javadoc.io/static/com.meeshkan/http-types/0.4.0/com/meeshkan/http/types/HttpExchangeReader.html) HTTP Types recordings can be read for processing:
```java
InputStream input = getClass().getResourceAsStream("/sample.jsonl");

HttpExchangeReader
    .fromJsonLines(input)
    .filter(exchange -> exchange.getResponse().getStatusCode() == 200)
    .forEach(exchange -> {
        HttpRequest request = exchange.getRequest();
        HttpUrl url = request.getUrl();
        HttpResponse response = exchange.getResponse();

        System.out.println("A " + request.getMethod() + " request to " +
            url.getHost() + " with response body " + response.getBody());
});
```
