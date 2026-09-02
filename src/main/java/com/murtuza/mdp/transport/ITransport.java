package com.murtuza.mdp.transport;

import com.murtuza.mdp.event.MarketDataEvent;

public interface ITransport
{
    void write(long time, String symbol, double derived);
}
