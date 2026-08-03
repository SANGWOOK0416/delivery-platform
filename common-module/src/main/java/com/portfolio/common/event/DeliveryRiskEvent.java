package com.portfolio.common.event;

import java.util.Objects;

public record DeliveryRiskEvent(Long orderId, String address, int precipitationType) {

    public DeliveryRiskEvent {
        Objects.requireNonNull(orderId, "orderId must not be null");
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("address must not be blank");
        }
    }
}
