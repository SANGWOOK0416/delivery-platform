package com.portfolio.weather.client;

/**
 * Resolves a free-text delivery address to a coordinate. {@link KakaoGeocodingClient} is the
 * real implementation (Kakao Local API); {@link StubGeocodingClient} stands in for it under the
 * "loadtest" profile so load tests never call the real external API.
 */
public interface GeocodingClient {

    GeoCoordinate geocode(String address);
}
