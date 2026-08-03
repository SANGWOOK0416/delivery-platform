package com.portfolio.weather.consumer;

import com.portfolio.common.event.DeliveryRiskEvent;
import com.portfolio.common.event.KafkaTopics;
import com.portfolio.common.event.OrderCreatedEvent;
import com.portfolio.weather.producer.DeliveryRiskProducer;
import com.portfolio.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherConsumer {

    private final WeatherService weatherService;
    private final DeliveryRiskProducer deliveryRiskProducer;

    @KafkaListener(topics = KafkaTopics.ORDER_EVENTS, groupId = "${spring.kafka.consumer.group-id}")
    public void consumeOrderEvent(OrderCreatedEvent orderEvent) {
        DeliveryRiskEvent deliveryRiskEvent = weatherService.analyzeDeliveryRisk(orderEvent);
        deliveryRiskProducer.sendDeliveryRiskEvent(deliveryRiskEvent);

        log.info("Processed order event. orderId={}", orderEvent.orderId());
    }
}
