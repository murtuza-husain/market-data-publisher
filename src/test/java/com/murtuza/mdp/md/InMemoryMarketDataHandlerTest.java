package com.murtuza.mdp.md;

import com.murtuza.mdp.event.MarketDataEvent;
import com.murtuza.mdp.transport.NoOpTransport;
import org.junit.Test;

import static org.junit.Assert.*;

public class InMemoryMarketDataHandlerTest
{
    @Test
    public void testValidation()
    {
        InMemoryMarketDataHandler sut = new InMemoryMarketDataHandler(new NoOpTransport(), 100);

        // Time validation
        assertTrue(sut.validated(1L, "ALPHA", MarketDataEventType.ADJUSTMENT, 1.0));
        assertFalse(sut.validated(0L, "ALPHA", MarketDataEventType.ADJUSTMENT, 1.0));
        assertFalse(sut.validated(-1L, "ALPHA", MarketDataEventType.ADJUSTMENT, 1.0));

        // Symbol validation
        assertTrue(sut.validated(1L, "ALPHA", MarketDataEventType.ADJUSTMENT, 1.0));
        assertTrue(sut.validated(1L, "BRAVO", MarketDataEventType.ADJUSTMENT, 1.0));
        assertTrue(sut.validated(1L, "CHARLIE", MarketDataEventType.ADJUSTMENT, 1.0));
        assertTrue(sut.validated(1L, "DELTA", MarketDataEventType.ADJUSTMENT, 1.0));
        assertTrue(sut.validated(1L, "ECHO", MarketDataEventType.ADJUSTMENT, 1.0));
        assertFalse(sut.validated(1L, "FOXTROT", MarketDataEventType.ADJUSTMENT, 1.0));

        // Event type validation
        assertTrue(sut.validated(1L, "ALPHA", MarketDataEventType.ADJUSTMENT, 1.0));
        assertTrue(sut.validated(1L, "ALPHA", MarketDataEventType.BASE_RATE, 1.0));
        assertTrue(sut.validated(1L, "ALPHA", MarketDataEventType.SPREAD, 1.0));
        assertFalse(sut.validated(1L, "ALPHA", MarketDataEventType.NONE, 1.0));

        // Value validation
        assertTrue(sut.validated(1L, "ALPHA", MarketDataEventType.ADJUSTMENT, 1.0));
        assertTrue(sut.validated(1L, "ALPHA", MarketDataEventType.ADJUSTMENT, -1.0));
        assertFalse(sut.validated(1L, "ALPHA", MarketDataEventType.ADJUSTMENT, 0.0));
        assertFalse(sut.validated(1L, "ALPHA", MarketDataEventType.ADJUSTMENT, Double.NaN));
    }

    @Test
    public void testDiscardOutOfOrderEvent()
    {
        InMemoryMarketDataHandler sut = new InMemoryMarketDataHandler(new NoOpTransport(), 100);
        assertTrue(sut.onEvent(1L, "ALPHA", MarketDataEventType.ADJUSTMENT, 1.0));
        assertTrue(sut.onEvent(2L, "ALPHA", MarketDataEventType.ADJUSTMENT, 1.0));
        assertFalse(sut.onEvent(1L, "ALPHA", MarketDataEventType.ADJUSTMENT, 1.0));
    }

    @Test
    public void testShouldPublish()
    {
        MarketDataEvent event = new MarketDataEvent("ALPHA");
        event.setDerived(1.1);

        assertTrue(InMemoryMarketDataHandler.shouldPublish(1.0, event));
        assertTrue(InMemoryMarketDataHandler.shouldPublish(1.00001, event));

        // Small change
        event.setDerived(1.0);
        assertFalse(InMemoryMarketDataHandler.shouldPublish(1.000001, event));

        // Same value
        event.setDerived(1.000001);
        assertFalse(InMemoryMarketDataHandler.shouldPublish(1.000001, event));
    }
}