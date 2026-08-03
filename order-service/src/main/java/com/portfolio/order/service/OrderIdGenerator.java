package com.portfolio.order.service;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class OrderIdGenerator {

    private final AtomicLong sequence = new AtomicLong(1_000L);

    public Long nextId() {
        return sequence.incrementAndGet();
    }
}
