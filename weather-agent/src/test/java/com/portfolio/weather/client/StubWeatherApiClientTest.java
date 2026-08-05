package com.portfolio.weather.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StubWeatherApiClientTest {

    @Test
    void alwaysReturnsNoPrecipitation() {
        StubWeatherApiClient client = new StubWeatherApiClient(0L);

        int precipitationType = client.fetchCurrentPrecipitationType(60, 127);

        assertThat(precipitationType).isZero();
    }
}
