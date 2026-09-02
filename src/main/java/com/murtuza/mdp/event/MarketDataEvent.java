package com.murtuza.mdp.event;

import com.murtuza.mdp.md.MarketDataEventType;

public class MarketDataEvent
{
    private final String symbol;
    private long timestampMillis;
    private double baseRate;
    private double spread;
    private double adjustment;
    private volatile int seq;
    private double derived;

    public MarketDataEvent(String symbol)
    {
        this.symbol = symbol;
    }

    public void update(long timestampMillis, short type, double value)
    {
        seq++;
        switch (type)
        {
            case MarketDataEventType.ADJUSTMENT:
                adjustment = value;
                break;
            case MarketDataEventType.BASE_RATE:
                baseRate = value;
                break;
            case MarketDataEventType.SPREAD:
                spread = value;
                break;
        }
        this.timestampMillis = timestampMillis;
        seq++;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public long getTimestampMillis()
    {
        return timestampMillis;
    }

    public double getBaseRate()
    {
        return baseRate;
    }

    public double getSpread()
    {
        return spread;
    }

    public double getAdjustment()
    {
        return adjustment;
    }

    public int getSeq()
    {
        return seq;
    }

    public double getDerived()
    {
        return derived;
    }

    public void setDerived(double derived)
    {
        this.derived = derived;
    }
}
