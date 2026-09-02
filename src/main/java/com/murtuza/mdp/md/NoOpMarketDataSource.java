package com.murtuza.mdp.md;

import com.murtuza.mdp.metrics.IMetricsRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoOpMarketDataSource implements IMarketDataSource
{
    private static final Logger LOGGER = LoggerFactory.getLogger(NoOpMarketDataSource.class);

    @Override
    public void start()
    {
        LOGGER.info("*** NO OP MARKET DATA SOURCE PLACHOLDER ***");
    }

    @Override
    public void registerHandler(IMetricsRecorder handler)
    {

    }

    @Override
    public void close() throws Exception
    {

    }
}
