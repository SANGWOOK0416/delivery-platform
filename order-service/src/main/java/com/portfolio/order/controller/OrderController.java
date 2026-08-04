package com.portfolio.order.controller;

import com.portfolio.common.response.ApiResponse;
import com.portfolio.order.dto.OrderAcceptedResponse;
import com.portfolio.order.dto.OrderRequest;
import com.portfolio.order.dto.OrderResponse;
import com.portfolio.order.service.OrderEventBroadcaster;
import com.portfolio.order.service.OrderQueryService;
import com.portfolio.order.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderQueryService orderQueryService;
    private final OrderEventBroadcaster orderEventBroadcaster;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderAcceptedResponse>> createOrder(
            @Valid @RequestBody OrderRequest request
    ) {
        Long orderId = orderService.createOrder(request);

        return ResponseEntity.accepted()
                .body(ApiResponse.accepted(new OrderAcceptedResponse(orderId)));
    }

    @GetMapping
    public ApiResponse<List<OrderResponse>> listOrders() {
        return ApiResponse.success(orderQueryService.findRecentOrders());
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return orderEventBroadcaster.subscribe();
    }
}
