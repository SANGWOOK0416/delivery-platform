package com.portfolio.weather.client;

/**
 * Looks up the current precipitation type (PTY) for a KMA forecast grid cell.
 * {@link KmaWeatherClient} is the real implementation (KMA 초단기실황 API);
 * {@link StubWeatherApiClient} stands in for it under the "loadtest" profile so load tests
 * never call the real external API.
 */
public interface WeatherApiClient {

    int fetchCurrentPrecipitationType(int nx, int ny);
}
