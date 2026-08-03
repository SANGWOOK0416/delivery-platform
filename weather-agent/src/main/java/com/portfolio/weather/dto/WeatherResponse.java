package com.portfolio.weather.dto;

import com.portfolio.common.event.DeliveryRiskEvent;

public record WeatherResponse(String status, String riskLevel, int delayMinutes) {

    public static WeatherResponse from(DeliveryRiskEvent event) {
        return switch (event.precipitationType()) {
            case 0 -> new WeatherResponse("CLEAR", "LOW", 0);
            case 1 -> new WeatherResponse("RAIN", "HIGH", 15);
            case 2 -> new WeatherResponse("RAIN_AND_SNOW", "HIGH", 20);
            case 3 -> new WeatherResponse("SNOW", "HIGH", 20);
            case 4 -> new WeatherResponse("SHOWER", "MEDIUM", 10);
            default -> new WeatherResponse("UNKNOWN", "MEDIUM", 10);
        };
    }
}
