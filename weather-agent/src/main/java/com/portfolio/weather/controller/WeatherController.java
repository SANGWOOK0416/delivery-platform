package com.portfolio.weather.controller;

import com.portfolio.common.event.OrderCreatedEvent;
import com.portfolio.common.response.ApiResponse;
import com.portfolio.weather.dto.WeatherResponse;
import com.portfolio.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/risk")
    public ApiResponse<WeatherResponse> getWeatherRisk(@RequestParam String address) {
        OrderCreatedEvent previewOrder = new OrderCreatedEvent(0L, 0L, address);
        WeatherResponse response = WeatherResponse.from(weatherService.analyzeDeliveryRisk(previewOrder));
        return ApiResponse.success(response);
    }
}
