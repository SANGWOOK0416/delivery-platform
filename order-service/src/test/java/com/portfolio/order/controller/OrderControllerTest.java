package com.portfolio.order.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.portfolio.common.response.ApiResponse;
import com.portfolio.order.dto.OrderAcceptedResponse;
import com.portfolio.order.dto.OrderRequest;
import com.portfolio.order.dto.OrderResponse;
import com.portfolio.order.service.OrderEventBroadcaster;
import com.portfolio.order.service.OrderQueryService;
import com.portfolio.order.service.OrderService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

class OrderControllerTest {

    private final OrderService orderService = Mockito.mock(OrderService.class);
    private final OrderQueryService orderQueryService = Mockito.mock(OrderQueryService.class);
    private final OrderEventBroadcaster orderEventBroadcaster = Mockito.mock(OrderEventBroadcaster.class);
    private final OrderController controller =
            new OrderController(orderService, orderQueryService, orderEventBroadcaster);

    @Test
    void acceptsTheOrderAndReturnsTheGeneratedOrderId() {
        OrderRequest request = new OrderRequest(10L, "Seoul, Jongno-gu");
        when(orderService.createOrder(request)).thenReturn(1001L);

        ResponseEntity<ApiResponse<OrderAcceptedResponse>> response = controller.createOrder(request);

        assertThat(response.getStatusCode().value()).isEqualTo(202);
        assertThat(response.getBody().status()).isEqualTo(202);
        assertThat(response.getBody().data().orderId()).isEqualTo(1001L);
    }

    @Test
    void listsRecentOrdersFromTheQueryService() {
        List<OrderResponse> orders = List.of(new OrderResponse(1001L, 10L, "Seoul", Instant.now()));
        when(orderQueryService.findRecentOrders()).thenReturn(orders);

        ApiResponse<List<OrderResponse>> response = controller.listOrders();

        assertThat(response.status()).isEqualTo(200);
        assertThat(response.data()).isEqualTo(orders);
    }
}
