package com.murtuza.mdp.md;

import org.junit.Test;

import static org.junit.Assert.*;

public class MarketDataEventTypeTest
{
    @Test
    public void testParse()
    {
        // Lower case
        assertEquals(MarketDataEventType.ADJUSTMENT, MarketDataEventType.parse("adjustment"));
        assertEquals(MarketDataEventType.BASE_RATE, MarketDataEventType.parse("base_rate"));
        assertEquals(MarketDataEventType.SPREAD, MarketDataEventType.parse("spread"));

        // Upper case
        assertEquals(MarketDataEventType.NONE, MarketDataEventType.parse("ADJUSTMENT"));
        assertEquals(MarketDataEventType.NONE, MarketDataEventType.parse("BASE_RATE"));
        assertEquals(MarketDataEventType.NONE, MarketDataEventType.parse("SPREAD"));

        // Missing _
        assertEquals(MarketDataEventType.NONE, MarketDataEventType.parse("baserate"));
    }
}