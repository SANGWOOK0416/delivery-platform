package com.portfolio.weather.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Stands in for {@link KmaWeatherClient} during load tests so no real call ever reaches the
 * KMA API. Always returns "no precipitation" after a simulated delay — a zero-latency stub
 * would understate real request latency and overstate throughput.
 */
@Component
@Profile("loadtest")
public class StubWeatherApiClient implements WeatherApiClient {

    private static final int NO_PRECIPITATION = 0;

    private final long simulatedLatencyMs;

    public StubWeatherApiClient(@Value("${loadtest.weather.simulated-latency-ms:80}") long simulatedLatencyMs) {
        this.simulatedLatencyMs = simulatedLatencyMs;
    }

    @Override
    public int fetchCurrentPrecipitationType(int nx, int ny) {
        if (simulatedLatencyMs > 0) {
            try {
                Thread.sleep(simulatedLatencyMs);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
        return NO_PRECIPITATION;
    }
}
