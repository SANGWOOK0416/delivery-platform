package com.portfolio.weather.producer;

import com.portfolio.common.event.DeliveryRiskEvent;
import com.portfolio.common.event.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryRiskProducer {

    private final KafkaTemplate<String, DeliveryRiskEvent> kafkaTemplate;

    public void sendDeliveryRiskEvent(DeliveryRiskEvent event) {
        kafkaTemplate.send(KafkaTopics.DELIVERY_RISK_EVENTS, event.orderId().toString(), event)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error("Failed to publish delivery-risk event. orderId={}", event.orderId(), exception);
                        return;
                    }

                    log.info("Published delivery-risk event. orderId={}, partition={}, offset={}",
                            event.orderId(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                });
    }
}
