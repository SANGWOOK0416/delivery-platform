package com.portfolio.order.controller;

import com.portfolio.common.response.ApiResponse;
import com.portfolio.order.dto.OrderAcceptedResponse;
import com.portfolio.order.dto.OrderRequest;
import com.portfolio.order.service.OrderService;
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

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderAcceptedResponse>> createOrder(
            @Valid @RequestBody OrderRequest request
    ) {
        Long orderId = orderService.createOrder(request);

        return ResponseEntity.accepted()
                .body(ApiResponse.accepted(new OrderAcceptedResponse(orderId)));
    }
}
