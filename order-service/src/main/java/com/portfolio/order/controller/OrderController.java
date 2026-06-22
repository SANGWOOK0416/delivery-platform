package com.portfolio.order.controller;

import com.portfolio.common.response.ApiResponse;
import com.portfolio.order.dto.OrderRequest;
import com.portfolio.order.dto.OrderEvent;
import com.portfolio.order.producer.OrderProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProducer orderProducer;

    @PostMapping
    public ApiResponse<String> createOrder(@RequestBody OrderRequest request) {
        Long mockOrderId = 1004L;

        // 1. 카프카 전송용 이벤트 객체 생성
        OrderEvent orderEvent = new OrderEvent(
            mockOrderId,
            request.getCustomerId(),
            request.getAddress()
        );

        // 2. 카프카로 이벤트 발행 (대기 시간 없음)
        orderProducer.sendOrderEvent(orderEvent);

        // 3. 시스템 연동을 위해 클라이언트에게 즉시 성공 응답 반환
        return ApiResponse.success("주문 접수가 완료되었습니다. 배달 위험도 분석 및 알림 전송이 비동기로 처리됩니다.");
    }
}