package com.murtuza.mdp.metrics;

import com.murtuza.mdp.md.IMarketDataEventHandler;

public interface IMetricsRecorder extends IMarketDataEventHandler
{
    void setup();
    void recordFailedEvent();
}