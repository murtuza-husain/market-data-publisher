package com.murtuza.mdp;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.murtuza.mdp.md.IMarketDataEventHandler;
import com.murtuza.mdp.metrics.IMetricsRecorder;
import com.murtuza.mdp.metrics.PrometheusHttpServer;
import com.murtuza.mdp.metrics.PrometheusMetricsRecorder;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

public class MetricsModule extends AbstractModule
{
    @Provides
    @Singleton
    public PrometheusMeterRegistry providePrometheusMeterRegistry()
    {
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }

    @Provides
    @Singleton
    public IMetricsRecorder providePrometheusMetricsRecorder(PrometheusMeterRegistry registry, IMarketDataEventHandler handler)
    {
        return new PrometheusMetricsRecorder(registry, handler);
    }

    @Provides
    @Singleton
    public PrometheusHttpServer providesPrometheusHttpServer(PrometheusMeterRegistry registry)
    {
        return new PrometheusHttpServer(registry, 8092); //TODO make port configurable
    }
}
