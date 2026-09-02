package com.murtuza.mdp;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.murtuza.mdp.md.IMarketDataSource;
import com.murtuza.mdp.metrics.IMetricsRecorder;
import com.murtuza.mdp.metrics.PrometheusHttpServer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public class ApplicationRunner
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationRunner.class);

    public static void initialiseAndRun(String[] args)
    {
        Thread.setDefaultUncaughtExceptionHandler((t,e) -> {
            LOGGER.error("Uncaught exception in thread {}", t.getName(), e);
            System.exit(-1);
        });

        AtomicReference<ApplicationRunner> runner = new AtomicReference<>();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutdown hook called");
            if (runner.get() != null)
            {
                try
                {
                    runner.get().stop();
                }
                catch (Exception e)
                {
                    LOGGER.error("Exception whilst shutting down applicaion runner", e);
                    System.exit(-1);
                }
                System.exit(1);
            }
        }));

        runner.set(createRunner(args));
        runner.get().start();
        LOGGER.info("Application started");
    }

    private static ApplicationRunner createRunner(String[] args)
    {
        // Production stage allows @Singleton components to be eagerly instantiated
        Injector injector = Guice.createInjector(
                Stage.PRODUCTION,
                new MarketDataModule(args),
                new MetricsModule(),
                new ServiceModule()
        );
        ApplicationRunner runner = injector.getInstance(ApplicationRunner.class);
        runner.injector = injector;
        return runner;
    }

    private Injector injector;

    public void start()
    {
        // Metrics
        try
        {
            injector.getInstance(IMetricsRecorder.class).setup();
            injector.getInstance(PrometheusHttpServer.class).start();
        } catch (Exception e) {}

        // Outbound transport


        // Event broker


        // MD source
        injector.getInstance(IMarketDataSource.class).start();

        LOGGER.info("Runner start complete");
    }

    public void stop()
    {
        // Close in reverse order to start
        // MD source
        try
        {
            injector.getInstance(IMarketDataSource.class).close();
        }
        catch (Exception e) {}

        // Event broker


        // Outbound transport


        // Metrics
        injector.getInstance(PrometheusHttpServer.class).stop();
    }
}
