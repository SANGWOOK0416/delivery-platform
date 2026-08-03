package com.portfolio.weather.service;

import com.portfolio.common.event.DeliveryRiskEvent;
import com.portfolio.common.event.OrderCreatedEvent;
import com.portfolio.weather.client.KmaWeatherClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final KmaWeatherClient kmaWeatherClient;

    public DeliveryRiskEvent analyzeDeliveryRisk(OrderCreatedEvent orderEvent) {
        int precipitationType = kmaWeatherClient.fetchCurrentPrecipitationType();
        return new DeliveryRiskEvent(
                orderEvent.orderId(),
                orderEvent.address(),
                precipitationType
        );
    }
}
