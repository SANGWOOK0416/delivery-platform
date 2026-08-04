package com.portfolio.order.service;

import com.portfolio.common.event.OrderCreatedEvent;
import com.portfolio.order.dto.OrderRequest;
import com.portfolio.order.dto.OrderResponse;
import com.portfolio.order.entity.OrderEntity;
import com.portfolio.order.producer.OrderProducer;
import com.portfolio.order.repository.OrderRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Persists the order before publishing its event. If the DB write fails, the API can return
 * an honest 5xx instead of accepting an order that doesn't durably exist anywhere. If the
 * publish fails after a successful save, the order is still durably recorded and recoverable —
 * the reverse ordering would risk an event firing for an order with no permanent record at all.
 * This is not fully atomic (no transactional outbox) — see README "알려진 한계".
 *
 * The order id comes from the database's identity column, not an in-memory counter, so it
 * stays unique across restarts — the id is only known once the save has actually happened.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;
    private final OrderEventBroadcaster orderEventBroadcaster;

    public Long createOrder(OrderRequest request) {
        OrderEntity savedOrder = orderRepository.save(
                new OrderEntity(null, request.customerId(), request.address(), Instant.now()));
        Long orderId = savedOrder.getId();

        OrderCreatedEvent event = new OrderCreatedEvent(orderId, request.customerId(), request.address());
        orderProducer.sendOrderCreatedEvent(event);

        orderEventBroadcaster.broadcastNewOrder(OrderResponse.from(savedOrder));

        return orderId;
    }
}
