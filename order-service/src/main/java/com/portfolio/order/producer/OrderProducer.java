package com.portfolio.order.producer;

import com.portfolio.common.event.KafkaTopics;
import com.portfolio.common.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        kafkaTemplate.send(KafkaTopics.ORDER_EVENTS, event.orderId().toString(), event)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error("Failed to publish order event. orderId={}", event.orderId(), exception);
                        return;
                    }

                    log.info("Published order event. orderId={}, partition={}, offset={}",
                            event.orderId(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                });
    }
}
