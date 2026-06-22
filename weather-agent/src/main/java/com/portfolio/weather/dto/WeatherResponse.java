package com.portfolio.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WeatherResponse {
    private String status;       // 날씨 상태 (예: RAINY, SUNNY, SNOWY)
    private String riskLevel;   // 배달 위험도 (HIGH, MEDIUM, LOW)
    private int delayMinutes;   // 예상 지연 시간 (분 단위)
}