package com.portfolio.weather.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.portfolio.common.event.DeliveryRiskEvent;
import com.portfolio.common.event.OrderCreatedEvent;
import com.portfolio.weather.client.GeocodingException;
import com.portfolio.weather.client.KakaoGeocodingClient;
import com.portfolio.weather.client.KmaGridConverter;
import com.portfolio.weather.client.KmaWeatherClient;
import com.portfolio.weather.config.KmaApiProperties;
import com.portfolio.weather.dto.WeatherResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class WeatherServiceTest {

    private final KakaoGeocodingClient kakaoGeocodingClient = Mockito.mock(KakaoGeocodingClient.class);
    private final KmaGridConverter kmaGridConverter = Mockito.mock(KmaGridConverter.class);
    private final KmaWeatherClient kmaWeatherClient = Mockito.mock(KmaWeatherClient.class);
    private final KmaApiProperties kmaApiProperties = new KmaApiProperties(
            "test-service-key", "https://example.com", new KmaApiProperties.Grid(60, 127)
    );

    private final WeatherService weatherService =
            new WeatherService(kakaoGeocodingClient, kmaGridConverter, kmaWeatherClient, kmaApiProperties);

    @Test
    void usesTheGeocodedGridCoordinateWhenTheAddressResolvesSuccessfully() {
        OrderCreatedEvent orderEvent = new OrderCreatedEvent(101L, 202L, "서울시 강남구 테헤란로 123");
        KakaoGeocodingClient.GeoCoordinate coordinate = new KakaoGeocodingClient.GeoCoordinate(37.5006, 127.0366);
        when(kakaoGeocodingClient.geocode(orderEvent.address())).thenReturn(coordinate);
        when(kmaGridConverter.toGrid(coordinate.latitude(), coordinate.longitude()))
                .thenReturn(new KmaApiProperties.Grid(61, 120));
        when(kmaWeatherClient.fetchCurrentPrecipitationType(61, 120)).thenReturn(1);

        DeliveryRiskEvent event = weatherService.analyzeDeliveryRisk(orderEvent);

        assertThat(event).isEqualTo(new DeliveryRiskEvent(101L, orderEvent.address(), 1));
        assertThat(WeatherResponse.from(event)).isEqualTo(new WeatherResponse("RAIN", "HIGH", 15));
    }

    @Test
    void fallsBackToTheDefaultGridWhenGeocodingFails() {
        OrderCreatedEvent orderEvent = new OrderCreatedEvent(101L, 202L, "존재하지 않는 주소");
        when(kakaoGeocodingClient.geocode(orderEvent.address()))
                .thenThrow(new GeocodingException("주소 검색 결과가 없습니다."));
        when(kmaWeatherClient.fetchCurrentPrecipitationType(60, 127)).thenReturn(0);

        DeliveryRiskEvent event = weatherService.analyzeDeliveryRisk(orderEvent);

        assertThat(event).isEqualTo(new DeliveryRiskEvent(101L, orderEvent.address(), 0));
        assertThat(WeatherResponse.from(event)).isEqualTo(new WeatherResponse("CLEAR", "LOW", 0));
    }
}
