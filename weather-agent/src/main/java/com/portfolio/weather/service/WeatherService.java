package com.portfolio.weather.service;

import com.portfolio.common.event.DeliveryRiskEvent;
import com.portfolio.common.event.OrderCreatedEvent;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private static final int NO_PRECIPITATION = 0;

    public DeliveryRiskEvent analyzeDeliveryRisk(OrderCreatedEvent orderEvent) {
        // The KMA client will replace this safe fallback when live weather lookup is introduced.
        return new DeliveryRiskEvent(
                orderEvent.orderId(),
                orderEvent.address(),
                NO_PRECIPITATION
        );
    }
}
