package com.portfolio.weather.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Stands in for {@link KakaoGeocodingClient} during load tests so no real call ever reaches
 * the Kakao Local API. Always "succeeds" with a fixed coordinate (Seoul City Hall) after a
 * simulated delay — a zero-latency stub would understate real request latency and overstate
 * throughput.
 */
@Component
@Profile("loadtest")
public class StubGeocodingClient implements GeocodingClient {

    private static final GeoCoordinate SEOUL_CITY_HALL = new GeoCoordinate(37.5665, 126.9780);

    private final long simulatedLatencyMs;

    public StubGeocodingClient(@Value("${loadtest.geocoding.simulated-latency-ms:60}") long simulatedLatencyMs) {
        this.simulatedLatencyMs = simulatedLatencyMs;
    }

    @Override
    public GeoCoordinate geocode(String address) {
        if (simulatedLatencyMs > 0) {
            try {
                Thread.sleep(simulatedLatencyMs);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
        return SEOUL_CITY_HALL;
    }
}
