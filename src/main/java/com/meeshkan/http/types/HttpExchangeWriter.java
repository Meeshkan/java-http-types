package com.meeshkan.http.types;

import org.jetbrains.annotations.NotNull;
import org.json.JSONWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Writes {@link HttpExchange HTTP exchanges} in the <a href="https://meeshkan.github.io/http-types/">HTTP Types JSON Lines format</a>.
 * <p>
 * Output written using this writer can be read back using a {@link HttpExchangeReader}.
 */
public final class HttpExchangeWriter implements Closeable {
    @NotNull
    private final BufferedWriter bufferedWriter;
    boolean first = true;

    /**
     * Creates a HTTP exchange writer that uses the specified output stream.
     *
     * @param out Output where to write the serialized HTTP exchanges
     */
    public HttpExchangeWriter(@NotNull OutputStream out) {
        this(new OutputStreamWriter(out, StandardCharsets.UTF_8));
    }

    /**
     * Creates a HTTP exchange writer that uses the specified output writer.
     *
     * @param out Output where to write the serialized HTTP exchanges
     */
    public HttpExchangeWriter(@NotNull Writer out) {
        if (out instanceof BufferedWriter) {
            this.bufferedWriter = (BufferedWriter) out;
        } else {
            this.bufferedWriter = new BufferedWriter(out);
        }
    }

    /**
     * Writes a HTTP exchange as a single JSON formatted line.
     *
     * @param exchange The HTTP exchange to write
     * @see #writeAll(Collection)
     */
    public void write(@NotNull HttpExchange exchange) {
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

    /**
     * Writes multiple HTTP Exchanges, each formatted in JSON format on a single line.
     *
     * @param exchanges The HTTP exchanges to write
     * @see #write(HttpExchange)
     */
    public void writeAll(@NotNull Collection<HttpExchange> exchanges) {
        for (HttpExchange exchange : exchanges) {
            write(exchange);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        bufferedWriter.close();
    }

}
