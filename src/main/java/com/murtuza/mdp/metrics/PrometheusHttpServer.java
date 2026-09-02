package com.murtuza.mdp.metrics;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class PrometheusHttpServer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PrometheusHttpServer.class);

    private final PrometheusMeterRegistry registry;
    private final int port;

    private HttpServer server;

    public PrometheusHttpServer(PrometheusMeterRegistry registry, int port)
    {
        this.registry = registry;
        this.port = port;
    }

    public void start() throws IOException
    {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/metrics", exchange -> {
            String scrapeOutput = registry.scrape(); // Prometheus text format
            byte[] bytes = scrapeOutput.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set("Content-Type", "text/plain; version=0.0.4");
            exchange.sendResponseHeaders(200, bytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });

        server.start();
        LOGGER.info("Prometheus HttpServer started");
    }

    public void stop()
    {
        if (server != null)
        {
            server.stop(port);
            LOGGER.info("Prometheus HttpServer stopped");
        }
    }
}
