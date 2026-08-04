package com.portfolio.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.portfolio.order.dto.OrderResponse;
import com.portfolio.order.entity.OrderEntity;
import com.portfolio.order.repository.OrderRepository;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;

class OrderQueryServiceTest {

    private final OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
    private final OrderQueryService orderQueryService = new OrderQueryService(orderRepository);

    @Test
    void mapsEntitiesToResponsesSortedByCreatedAtDescending() {
        OrderEntity entity = new OrderEntity(1001L, 10L, "Seoul", Instant.now());
        when(orderRepository.findAll(any(Sort.class))).thenReturn(List.of(entity));

        List<OrderResponse> responses = orderQueryService.findRecentOrders();

        assertThat(responses).containsExactly(OrderResponse.from(entity));
    }
}
