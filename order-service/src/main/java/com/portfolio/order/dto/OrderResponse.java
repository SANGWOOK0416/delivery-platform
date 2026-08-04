package com.portfolio.order.dto;

import com.portfolio.order.entity.OrderEntity;
import java.time.Instant;

public record OrderResponse(Long orderId, Long customerId, String address, Instant createdAt) {

    public static OrderResponse from(OrderEntity entity) {
        return new OrderResponse(entity.getId(), entity.getCustomerId(), entity.getAddress(), entity.getCreatedAt());
    }
}
