package com.portfolio.order.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.portfolio.common.response.ApiResponse;
import com.portfolio.order.dto.OrderAcceptedResponse;
import com.portfolio.order.dto.OrderRequest;
import com.portfolio.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

class OrderControllerTest {

    @Test
    void acceptsTheOrderAndReturnsTheGeneratedOrderId() {
        OrderService orderService = Mockito.mock(OrderService.class);
        OrderRequest request = new OrderRequest(10L, "Seoul, Jongno-gu");
        when(orderService.createOrder(request)).thenReturn(1001L);

        OrderController controller = new OrderController(orderService);

        ResponseEntity<ApiResponse<OrderAcceptedResponse>> response = controller.createOrder(request);

        assertThat(response.getStatusCode().value()).isEqualTo(202);
        assertThat(response.getBody().status()).isEqualTo(202);
        assertThat(response.getBody().data().orderId()).isEqualTo(1001L);
    }
}
