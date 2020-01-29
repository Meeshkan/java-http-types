package com.meeshkan.http.types;

import org.json.JSONWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class HttpExchangeWriter implements Closeable {
    private final BufferedWriter bufferedWriter;
    boolean first = true;

    public HttpExchangeWriter(OutputStream out) {
        this(new OutputStreamWriter(out, StandardCharsets.UTF_8));
    }

    public HttpExchangeWriter(Writer out) {
        if (out instanceof BufferedWriter) {
            this.bufferedWriter = (BufferedWriter) out;
        } else {
            this.bufferedWriter = new BufferedWriter(out);
        }
    }

    public void write(HttpExchange exchange) {
        if (first) {
            first = false;
        } else {
            try {
                bufferedWriter.write('\n');
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        JSONWriter writer = new JSONWriter(this.bufferedWriter);
        writer.object(); // Start main object.

        HttpRequest request = exchange.getRequest();
        writer.key("request");
        writer.object();
        writer.key("protocol").value(request.getUrl().getProtocol().name().toLowerCase());
        writer.key("method").value(request.getMethod().name().toLowerCase());
        writer.key("headers");
        writer.object();
        for (Map.Entry<String, List<String>> entry : request.getHeaders().asMap().entrySet()) {
            writer.key(entry.getKey()).value(entry.getValue());
        }
        writer.endObject();
        HttpUrl url = request.getUrl();
        writer.key("pathname").value(url.getPathname());
        writer.key("host").value(url.getHost());
        if (!url.getQueryParameters().isEmpty()) {
            writer.key("query");
            writer.object();
            for (Map.Entry<String, List<String>> entry : url.getQueryParameters().entrySet()) {
                writer.key(entry.getKey()).value(entry.getValue());
            }
            writer.endObject();
        }
        if (request.getBody() != null) {
            writer.key("body").value(request.getBody());
        }
        writer.endObject();

        HttpResponse response = exchange.getResponse();
        writer.key("response");
        writer.object(); // Start response.
        writer.key("statusCode").value(response.getStatusCode());
        writer.key("headers");
        writer.object(); // Start headers.
        for (Map.Entry<String, List<String>> entry : response.getHeaders().asMap().entrySet()) {
            writer.key(entry.getKey()).value(entry.getValue());
        }
        writer.endObject(); // End headers.
        if (response.getBody() != null) {
            writer.key("body").value(response.getBody());
        }
        writer.endObject(); // End response.

        writer.endObject(); // End main object.
    }

    public void writeAll(Collection<HttpExchange> exchanges) {
        for (HttpExchange exchange : exchanges) {
            write(exchange);
        }
    }

    @Override
    public void close() throws IOException {
        bufferedWriter.close();
    }
}
