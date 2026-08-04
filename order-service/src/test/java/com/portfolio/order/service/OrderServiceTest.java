package com.portfolio.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.portfolio.common.event.OrderCreatedEvent;
import com.portfolio.order.dto.OrderRequest;
import com.portfolio.order.entity.OrderEntity;
import com.portfolio.order.producer.OrderProducer;
import com.portfolio.order.repository.OrderRepository;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

class OrderServiceTest {

    private final OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
    private final OrderProducer orderProducer = Mockito.mock(OrderProducer.class);
    private final OrderService orderService = new OrderService(orderRepository, orderProducer);

    @Test
    void publishesTheEventWithTheIdAssignedByTheDatabaseSave() {
        OrderRequest request = new OrderRequest(10L, "Seoul, Jongno-gu");
        OrderEntity savedOrder = new OrderEntity(1001L, 10L, "Seoul, Jongno-gu", Instant.now());
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedOrder);

        Long orderId = orderService.createOrder(request);

        assertThat(orderId).isEqualTo(1001L);

        InOrder inOrder = Mockito.inOrder(orderRepository, orderProducer);
        inOrder.verify(orderRepository).save(any(OrderEntity.class));
        inOrder.verify(orderProducer).sendOrderCreatedEvent(any(OrderCreatedEvent.class));

        ArgumentCaptor<OrderEntity> entityCaptor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderRepository).save(entityCaptor.capture());
        assertThat(entityCaptor.getValue().getId()).isNull();
        assertThat(entityCaptor.getValue().getCustomerId()).isEqualTo(10L);
        assertThat(entityCaptor.getValue().getAddress()).isEqualTo("Seoul, Jongno-gu");

        ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(orderProducer).sendOrderCreatedEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isEqualTo(new OrderCreatedEvent(1001L, 10L, "Seoul, Jongno-gu"));
    }

    @Test
    void doesNotPublishWhenSavingFails() {
        when(orderRepository.save(any(OrderEntity.class))).thenThrow(new RuntimeException("db down"));

        assertThatThrownBy(() -> orderService.createOrder(new OrderRequest(11L, "Busan")))
                .isInstanceOf(RuntimeException.class);

        Mockito.verifyNoInteractions(orderProducer);
    }
}
