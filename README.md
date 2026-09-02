# market-data-publisher

## Design Choices

- Market Data is high volume therefore I have opted for low latency optimisations
 - Lock free handoff using a volatile `int` as a sequence lock
 - No allocation of objects on hot path (map pre-populated based on universe of known symbols)
- Metrics captured into Prometheus and can be visualised in Grafana via Docker script.
 - Counts on events received, how many were erroneous and how many were malformed.
 - Percentiles on time spent processing the event in market data thread.
 - This allows for auditing of performance and some insights into bad data rates.
- Lightweight Guice framework used for DI. 

## Assumptions
- Single source and 'stream' of data and a single consumer that is publishing out. This allows for lock free handoff via a standard `HashMap` for latency optimisation.
- Second parameter to be passed to application for the path to the output file.
- No throttler needed on output as this cadence is driven by the publishing rate. Only check made here is to see if the derived value has changed from the previous.

## Things Left Out

- Configuration to pass in publishing rate, Prometheus port and universe of known symbols. These are hardcoded for purposes of the exercise but a real world solution would need these values in configuration.
- Testing of Grafana and Prometheus integration.
- Testing via Docker (turns out my old laptop does not support the hypervisor needed :( )
- Using fixed point arithmetic and storing values as longs because the exercise is not focused on the math.
- Rotating log file configuration.

## Next Steps (Time Consraints)

- Implement publishing via `IEventBroker` leveraging LMAX disruptor for low latency handoff to a dedicated publishing thread.
- Add time management logic to step time based on the CSV input and from this model the step time when processing to simulate the publish time rather than using the last update time.
- Add callback logic to end the application once CSV file has been processed to avoid needing to manually send in a kill signal.
- Add better test coverage including tests that expand the boundaries of a single class and simulate data flow through the application.
- Test in Docker to see metrics in Grafana.
- Implement a Clickhouse output instead of CSV.
- Replay the CSV file outside of this applicaiton over a UDP stream into this application to better mimmic a real world scenario and more accurately model the publisher vs consumer time lag.

## Time Log

4 hour challenge. Time spent, in order

- ~30 minutes understanding the brief and planning the code level architecture.
- ~60 minutes setting up boilerplate code.
- ~60 minutes implementing the data ingestion and validation.
- ~30 minutes validating via tests (unit tests and debugging).
- ~30 minutes implementing the publisher.
- ~10 minutes on the README.

## AI Usage

Used Claude in the following capacity

- Docker file generation.
- Assistance with identifying the `skip` function in streaming API, used to skip the first line of the CSV input file.
- Log4J sample configuraton.
- Prometheus implementation of the metrics recorder and HTTP server.