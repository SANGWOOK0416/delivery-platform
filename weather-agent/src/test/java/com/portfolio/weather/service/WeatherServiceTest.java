package com.portfolio.weather.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.portfolio.common.event.DeliveryRiskEvent;
import com.portfolio.common.event.OrderCreatedEvent;
import com.portfolio.weather.dto.WeatherResponse;
import org.junit.jupiter.api.Test;

class WeatherServiceTest {

    private final WeatherService weatherService = new WeatherService();

    @Test
    void createsAConsistentNoPrecipitationRiskEvent() {
        DeliveryRiskEvent event = weatherService.analyzeDeliveryRisk(
                new OrderCreatedEvent(101L, 202L, "Seoul, Mapo-gu")
        );

        assertThat(event).isEqualTo(new DeliveryRiskEvent(101L, "Seoul, Mapo-gu", 0));
        assertThat(WeatherResponse.from(event))
                .isEqualTo(new WeatherResponse("CLEAR", "LOW", 0));
    }
}
