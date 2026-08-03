package com.portfolio.notification.consumer;

import com.portfolio.common.event.DeliveryRiskEvent;
import com.portfolio.common.event.KafkaTopics;
import com.portfolio.notification.service.KakaoMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final KakaoMessageService kakaoMessageService;

    @KafkaListener(topics = KafkaTopics.DELIVERY_RISK_EVENTS, groupId = "${spring.kafka.consumer.group-id}")
    public void consumeDeliveryRiskEvent(DeliveryRiskEvent event) {
        kakaoMessageService.sendDeliveryAlert(event);
        log.info("Processed delivery-risk event. orderId={}", event.orderId());
    }
}
