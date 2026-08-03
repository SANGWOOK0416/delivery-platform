package com.portfolio.order.controller;

import com.portfolio.common.event.OrderCreatedEvent;
import com.portfolio.common.response.ApiResponse;
import com.portfolio.order.dto.OrderAcceptedResponse;
import com.portfolio.order.dto.OrderRequest;
import com.portfolio.order.producer.OrderProducer;
import com.portfolio.order.service.OrderIdGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProducer orderProducer;
    private final OrderIdGenerator orderIdGenerator;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderAcceptedResponse>> createOrder(
            @Valid @RequestBody OrderRequest request
    ) {
        Long orderId = orderIdGenerator.nextId();
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, request.customerId(), request.address());

        orderProducer.sendOrderCreatedEvent(event);

        return ResponseEntity.accepted()
                .body(ApiResponse.accepted(new OrderAcceptedResponse(orderId)));
    }
}
