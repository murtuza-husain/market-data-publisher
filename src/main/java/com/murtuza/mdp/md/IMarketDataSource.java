package com.murtuza.mdp.md;

import com.murtuza.mdp.metrics.IMetricsRecorder;

public interface IMarketDataSource extends AutoCloseable
{
    void start();
    void registerHandler(IMetricsRecorder handler);
}