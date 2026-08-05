package com.portfolio.notification.service;

import com.portfolio.common.event.DeliveryRiskEvent;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Stands in for {@link KakaoMessageService} during load tests so no real call ever reaches the
 * Kakao Talk API. Simulates a configurable failure rate so the retry/DLQ path
 * (see {@code KafkaErrorHandlingConfig}) is actually exercised under load, not just the happy
 * path — a stub that never fails would make DLQ inflow ratio meaningless to measure.
 */
@Slf4j
@Service
@Profile("loadtest")
public class StubKakaoMessageSender implements KakaoMessageSender {

    private final double failureRate;
    private final long simulatedLatencyMs;

    public StubKakaoMessageSender(
            @Value("${loadtest.kakao.failure-rate:0.0}") double failureRate,
            @Value("${loadtest.kakao.simulated-latency-ms:100}") long simulatedLatencyMs) {
        this.failureRate = failureRate;
        this.simulatedLatencyMs = simulatedLatencyMs;
    }

    @Override
    public void sendDeliveryAlert(DeliveryRiskEvent event) {
        if (simulatedLatencyMs > 0) {
            try {
                Thread.sleep(simulatedLatencyMs);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }

        if (ThreadLocalRandom.current().nextDouble() < failureRate) {
            throw new StubKakaoSendException(
                    "Simulated Kakao send failure (loadtest profile). orderId=" + event.orderId());
        }

        log.debug("Simulated Kakao send. orderId={}", event.orderId());
    }
}
