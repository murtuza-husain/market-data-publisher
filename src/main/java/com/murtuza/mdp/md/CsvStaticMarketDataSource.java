package com.murtuza.mdp.md;

import com.murtuza.mdp.metrics.IMetricsRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class CsvStaticMarketDataSource implements IMarketDataSource
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CsvStaticMarketDataSource.class);

    private final String filePath;
    private IMetricsRecorder handler;

    public CsvStaticMarketDataSource(String filePath)
    {
        this.filePath = filePath;
    }

    @Override
    public void start()
    {
        LOGGER.info("Reading market data file {}", filePath);
        try (Stream<String> lines = Files.lines(Paths.get(filePath)))
        {
            lines.skip(1).forEach(this::onEvent);
        }
        catch (IOException e)
        {
            LOGGER.error("Unable to read source file at {}", filePath, e);
        }
        LOGGER.info("Market data file read complete");
    }

    private void onEvent(String line)
    {
        try
        {
            if (handler != null)
            {
                String[] columns = line.split(",");
                // Basic validation only here. We want to assert that we have something that looks appropriate, then
                // perform any further validation on the processing thread. This keeps the market data consumptions
                // thread free to deal with a potentially high frequency stream of updates.

                // Assert we have enough columns
                if (columns.length >= 4)
                {
                    long timestampMillis = Long.parseLong(columns[0]);
                    String symbol = columns[1];
                    short marketDataEventType = MarketDataEventType.parse(columns[2]);
                    double value = Double.parseDouble(columns[3]);
                    handler.onEvent(timestampMillis, symbol, marketDataEventType, value);
                }
                else
                {
                    handler.recordFailedEvent();
                }
            }
        }
        catch (Exception e)
        {
            handler.recordFailedEvent();
        }
    }

    @Override
    public void registerHandler(IMetricsRecorder handler)
    {
        this.handler = handler;
        LOGGER.info("Handler registered {}", handler.getClass());
    }

    @Override
    public void close() throws Exception
    {

    }
}
