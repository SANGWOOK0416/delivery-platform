package com.portfolio.order.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.portfolio.common.event.OrderCreatedEvent;
import com.portfolio.common.response.ApiResponse;
import com.portfolio.order.dto.OrderAcceptedResponse;
import com.portfolio.order.dto.OrderRequest;
import com.portfolio.order.producer.OrderProducer;
import com.portfolio.order.service.OrderIdGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;

class OrderControllerTest {

    @Test
    void acceptsOrderAndPublishesTypedEvent() {
        OrderProducer producer = org.mockito.Mockito.mock(OrderProducer.class);
        OrderController controller = new OrderController(producer, new OrderIdGenerator());

        ResponseEntity<ApiResponse<OrderAcceptedResponse>> response = controller.createOrder(
                new OrderRequest(10L, "Seoul, Jongno-gu")
        );

        ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(producer).sendOrderCreatedEvent(eventCaptor.capture());

        assertThat(response.getStatusCode().value()).isEqualTo(202);
        assertThat(response.getBody().status()).isEqualTo(202);
        assertThat(response.getBody().data().orderId()).isEqualTo(eventCaptor.getValue().orderId());
        assertThat(eventCaptor.getValue())
                .isEqualTo(new OrderCreatedEvent(eventCaptor.getValue().orderId(), 10L, "Seoul, Jongno-gu"));
    }
}
