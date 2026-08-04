package com.portfolio.weather.producer;

import com.portfolio.common.event.DeliveryRiskEvent;
import com.portfolio.common.event.KafkaTopics;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

/**
 * Publishes delivery-risk events synchronously (bounded by a timeout) so that a publish
 * failure surfaces as an exception to the calling {@code @KafkaListener} method, letting
 * the consumer's retry/dead-letter handling cover it — a fire-and-forget send would only
 * log the failure in a callback and let the original order-events offset commit anyway.
 */
@Slf4j
@Service
public class DeliveryRiskProducer {

    private static final Duration DEFAULT_SEND_TIMEOUT = Duration.ofSeconds(5);

    private final KafkaTemplate<String, DeliveryRiskEvent> kafkaTemplate;
    private final Duration sendTimeout;

    @Autowired
    public DeliveryRiskProducer(KafkaTemplate<String, DeliveryRiskEvent> kafkaTemplate) {
        this(kafkaTemplate, DEFAULT_SEND_TIMEOUT);
    }

    DeliveryRiskProducer(KafkaTemplate<String, DeliveryRiskEvent> kafkaTemplate, Duration sendTimeout) {
        this.kafkaTemplate = kafkaTemplate;
        this.sendTimeout = sendTimeout;
    }

    public void sendDeliveryRiskEvent(DeliveryRiskEvent event) {
        try {
            SendResult<String, DeliveryRiskEvent> result = kafkaTemplate
                    .send(KafkaTopics.DELIVERY_RISK_EVENTS, event.orderId().toString(), event)
                    .get(sendTimeout.toMillis(), TimeUnit.MILLISECONDS);

            log.info("Published delivery-risk event. orderId={}, partition={}, offset={}",
                    event.orderId(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
        } catch (TimeoutException exception) {
            throw new DeliveryRiskPublishException(
                    "Timed out publishing delivery-risk event. orderId=" + event.orderId(), exception);
        } catch (ExecutionException exception) {
            throw new DeliveryRiskPublishException(
                    "Failed to publish delivery-risk event. orderId=" + event.orderId(), exception.getCause());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new DeliveryRiskPublishException(
                    "Interrupted while publishing delivery-risk event. orderId=" + event.orderId(), exception);
        }
    }
}
