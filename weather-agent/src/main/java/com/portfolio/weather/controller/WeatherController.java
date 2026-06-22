package com.portfolio.weather.controller;

import com.portfolio.common.response.ApiResponse;
import com.portfolio.weather.dto.WeatherResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @GetMapping("/risk")
    public ApiResponse<WeatherResponse> getWeatherRisk(@RequestParam(value = "address") String address) {
        // 원래는 기상청 API를 호출해야 하지만, 1단계 뼈대 검증을 위해 가상의 하드코딩 데이터를 반환합니다.
        // 향후 실무형 코드로 고도화할 예정입니다.
        WeatherResponse mockResponse = new WeatherResponse("RAINY", "HIGH", 15);
        
        // 공통 모듈의 ApiResponse를 사용하여 규격을 포장합니다.
        return ApiResponse.success(mockResponse);
    }
}