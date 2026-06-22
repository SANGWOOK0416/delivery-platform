package com.portfolio.order.producer;

import com.portfolio.order.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProducer {

    // application.yml 설정을 바탕으로 스프링이 자동 주입해 주는 카프카 전송 템플릿
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    
    private static final String TOPIC = "order-events";

    public void sendOrderEvent(OrderEvent event) {
        log.info("Kafka로 주문 이벤트 발행 시작 -> orderId: {}", event.getOrderId());
        
        // 카프카 토픽으로 비동기 메시지 전송
        kafkaTemplate.send(TOPIC, event);
    }
}