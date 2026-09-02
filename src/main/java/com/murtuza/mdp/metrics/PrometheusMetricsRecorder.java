package com.murtuza.mdp.metrics;

import com.murtuza.mdp.md.IMarketDataEventHandler;
import com.murtuza.mdp.md.MarketDataEventType;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusMeterRegistry;

public class PrometheusMetricsRecorder implements IMetricsRecorder
{
    private final MeterRegistry registry;
    private final IMarketDataEventHandler handler;
    private Counter eventsReceived;
    private Counter eventsDropped;
    private Counter eventsMalformed;
    private Timer eventProcessingTime;

    public PrometheusMetricsRecorder(PrometheusMeterRegistry registry, IMarketDataEventHandler handler)
    {
        this.registry = registry;
        this.handler = handler;
    }

    @Override
    public void setup()
    {
        eventsReceived = Counter.builder("marketdata.events.received")
                .description("Count of total events received")
                .tag("event-count", "total-received")
                .register(registry);

        eventsDropped = Counter.builder("marketdata.events.dropped")
                .description("Count of total events dropped before processing")
                .tag("event-count", "total-dropped")
                .register(registry);

        eventsMalformed = Counter.builder("marketdata.events.malformed")
                .description("Count of total events received but malformed")
                .tag("event-count", "total-malformed")
                .register(registry);

        eventProcessingTime = Timer.builder("marketdata.events.processingTime")
                .description("Time to process an event in nanoseconds")
                .tag("event-processing", "latency")
                .publishPercentiles(0.5, 0.75, 0.9, 0.95, 0.99)
                .register(registry);
    }

    @Override
    public void recordFailedEvent()
    {
        eventsDropped.increment();
    }

    boolean processingCompleted = false;

    @Override
    public boolean onEvent(long timestampMillis, String symbol, short eventType, double value)
    {
        eventProcessingTime.record(() -> {
            try
            {
                processingCompleted = handler.onEvent(timestampMillis, symbol, eventType, value);
            }
            catch (Exception e)
            {
                processingCompleted = false;
            }
        });
        eventsReceived.increment();
        if (!processingCompleted)
        {
            eventsMalformed.increment();
        }
        return true;
    }
}
