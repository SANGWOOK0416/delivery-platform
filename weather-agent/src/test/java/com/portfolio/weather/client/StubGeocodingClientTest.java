package com.portfolio.weather.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StubGeocodingClientTest {

    @Test
    void alwaysReturnsAFixedCoordinateWithoutThrowing() {
        StubGeocodingClient client = new StubGeocodingClient(0L);

        GeoCoordinate coordinate = client.geocode("아무 주소나 상관없음");

        assertThat(coordinate.latitude()).isEqualTo(37.5665);
        assertThat(coordinate.longitude()).isEqualTo(126.9780);
    }
}
