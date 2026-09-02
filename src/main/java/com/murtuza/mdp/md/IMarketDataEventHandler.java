package com.murtuza.mdp.md;

public interface IMarketDataEventHandler
{
    boolean onEvent(long timestampMillis, String symbol, short eventType, double value);
}