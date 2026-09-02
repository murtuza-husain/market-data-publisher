package com.murtuza.mdp;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.murtuza.mdp.event.DisruptorEventBroker;
import com.murtuza.mdp.event.IEventBroker;
import com.murtuza.mdp.md.IMarketDataEventHandler;
import com.murtuza.mdp.md.InMemoryMarketDataHandler;

public class ServiceModule extends AbstractModule
{
    @Provides
    @Singleton
    public IEventBroker provideEventBroker()
    {
        return new DisruptorEventBroker();
    }


}
