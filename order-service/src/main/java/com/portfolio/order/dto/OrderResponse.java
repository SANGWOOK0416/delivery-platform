package com.portfolio.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private Long customerId;
    private String address;
    private String weatherStatus; // 날씨 서버에서 받아올 데이터
    private String riskLevel;     // 날씨 서버에서 받아올 데이터
    private int delayMinutes;     // 날씨 서버에서 받아올 데이터
}