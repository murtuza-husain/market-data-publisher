package com.murtuza.mdp.md;

import com.murtuza.mdp.event.MarketDataEvent;
import com.murtuza.mdp.transport.ITransport;
import org.omg.CORBA.IRObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InMemoryMarketDataHandler implements IMarketDataEventHandler
{
    private final String[] knownSymbols = {"ALPHA", "BRAVO", "CHARLIE", "DELTA", "ECHO"};
    private final Map<String, MarketDataEvent> eventMap;
    private final ITransport transport;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final int maxTries = 5;

    public InMemoryMarketDataHandler(ITransport transport, long publishRateMillis)
    {
        eventMap = new HashMap<>();
        //TODO known symbol universe should come from config/external source
        Arrays.stream(knownSymbols).forEach(s -> eventMap.put(s, new MarketDataEvent(s)));
        scheduler.scheduleAtFixedRate(this::calcAndPublish, 0, publishRateMillis, TimeUnit.MILLISECONDS);
        this.transport = transport;
    }

    @Override
    public boolean onEvent(long timestampMillis, String symbol, short eventType, double value)
    {
        if (validated(timestampMillis, symbol, eventType, value))
        {
            MarketDataEvent event = eventMap.get(symbol);
            // Discard stale, out of order events
            if (timestampMillis > event.getTimestampMillis())
            {
                event.update(timestampMillis, eventType, value);
                return true;
            }
        }
        return false;
    }

    protected boolean validated(long timestampMillis, String symbol, short eventType, double value)
    {
        // Basic sanity checks, real implementation would be more thorough
        return timestampMillis > 0 &&
                symbol != null &&
                !symbol.isEmpty() &&
                eventMap.containsKey(symbol) &&
                eventType > MarketDataEventType.NONE &&
                Double.isFinite(value) &&
                (Math.abs(value) > 0.0001);
    }

    public void calcAndPublish()
    {
        for (String key : knownSymbols)
        {
            calcAndPublish(eventMap.get(key));
        }
    }

    protected void calcAndPublish(MarketDataEvent event)
    {
        int i = 0;
        int seq1 = 0;
        int seq2 = 0;

        double adjustment = 0.0;
        double base_rate = 0.0;
        double spread = 0.0;
        long timestamp = 0L;

        // Sequence lock (lock free)
        while ((seq1 == 0 || seq2 == 0 || seq1 != seq2) && i < maxTries)
        {
            seq1 = event.getSeq();

            adjustment = event.getAdjustment();
            base_rate = event.getBaseRate();
            spread = event.getSpread();
            timestamp = event.getTimestampMillis();

            seq2 = event.getSeq();
            i++;
        }

        // We safely read within the max try limit
        if (seq1 > 0 && seq2 > 0 && seq1 == seq2)
        {
            double derived = base_rate + spread + adjustment;
            // Avoid publishing the same value again
            if (shouldPublish(derived, event))
            {
                event.setDerived(derived);
                transport.write(timestamp, event.getSymbol(), derived);
            }
        }
    }

    protected static boolean shouldPublish(double derived, MarketDataEvent event)
    {
        return Math.abs(derived - event.getDerived()) > 0.000001;
    }
}
