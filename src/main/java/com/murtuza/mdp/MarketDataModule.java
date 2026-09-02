package com.murtuza.mdp;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.murtuza.mdp.md.*;
import com.murtuza.mdp.metrics.IMetricsRecorder;
import com.murtuza.mdp.transport.CsvTransport;
import com.murtuza.mdp.transport.ITransport;
import com.murtuza.mdp.transport.NoOpTransport;

public class MarketDataModule extends AbstractModule
{
    private String inputFileName;
    private String outputFileName;

    public MarketDataModule(String[] args)
    {
        inputFileName = args.length > 0 ? args[0] : null;
        outputFileName = args.length > 1 ? args[1] : null;
    }

    @Provides
    @Singleton
    public IMarketDataSource providesMarketDataSource(IMetricsRecorder handler)
    {
        IMarketDataSource source;
        if (inputFileName != null)
        {
            source = new CsvStaticMarketDataSource(inputFileName);
        }
        else
        {
            // TODO add a UDP client implementation
            source = new NoOpMarketDataSource();
        }

        source.registerHandler(handler);
        return source;
    }

    @Provides
    @Singleton
    public ITransport provideTransport()
    {
        if (outputFileName != null)
        {
            return new CsvTransport(outputFileName);
        }
        return new NoOpTransport();
    }

    @Provides
    @Singleton
    public IMarketDataEventHandler provideMarketDataEventHandler(ITransport transport)
    {
        return new InMemoryMarketDataHandler(transport, 5);
    }
}
