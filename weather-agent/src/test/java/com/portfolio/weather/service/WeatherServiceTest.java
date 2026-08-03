package com.portfolio.weather.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.portfolio.common.event.DeliveryRiskEvent;
import com.portfolio.common.event.OrderCreatedEvent;
import com.portfolio.weather.client.KmaWeatherClient;
import com.portfolio.weather.dto.WeatherResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class WeatherServiceTest {

    private final KmaWeatherClient kmaWeatherClient = Mockito.mock(KmaWeatherClient.class);
    private final WeatherService weatherService = new WeatherService(kmaWeatherClient);

    @Test
    void mapsNoPrecipitationFromTheKmaClientOntoTheRiskEvent() {
        when(kmaWeatherClient.fetchCurrentPrecipitationType()).thenReturn(0);

        DeliveryRiskEvent event = weatherService.analyzeDeliveryRisk(
                new OrderCreatedEvent(101L, 202L, "Seoul, Mapo-gu")
        );

        assertThat(event).isEqualTo(new DeliveryRiskEvent(101L, "Seoul, Mapo-gu", 0));
        assertThat(WeatherResponse.from(event))
                .isEqualTo(new WeatherResponse("CLEAR", "LOW", 0));
    }

    @Test
    void mapsRainPrecipitationFromTheKmaClientOntoTheRiskEvent() {
        when(kmaWeatherClient.fetchCurrentPrecipitationType()).thenReturn(1);

        DeliveryRiskEvent event = weatherService.analyzeDeliveryRisk(
                new OrderCreatedEvent(101L, 202L, "Seoul, Mapo-gu")
        );

        assertThat(event).isEqualTo(new DeliveryRiskEvent(101L, "Seoul, Mapo-gu", 1));
        assertThat(WeatherResponse.from(event))
                .isEqualTo(new WeatherResponse("RAIN", "HIGH", 15));
    }
}
