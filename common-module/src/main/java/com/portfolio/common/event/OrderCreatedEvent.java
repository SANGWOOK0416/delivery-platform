package com.portfolio.common.event;

import java.util.Objects;

public record OrderCreatedEvent(Long orderId, Long customerId, String address) {

    public OrderCreatedEvent {
        Objects.requireNonNull(orderId, "orderId must not be null");
        Objects.requireNonNull(customerId, "customerId must not be null");
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("address must not be blank");
        }
    }
}
