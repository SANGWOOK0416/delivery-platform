package com.portfolio.weather.producer;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.portfolio.common.event.DeliveryRiskEvent;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

class DeliveryRiskProducerTest {

    private final KafkaTemplate<String, DeliveryRiskEvent> kafkaTemplate = Mockito.mock(KafkaTemplate.class);

    @Test
    void completesNormallyWhenTheSendSucceedsWithinTheTimeout() {
        DeliveryRiskEvent event = new DeliveryRiskEvent(1L, "Seoul", 0);
        when(kafkaTemplate.send(anyString(), anyString(), eq(event)))
                .thenReturn(CompletableFuture.completedFuture(successfulSendResult()));

        DeliveryRiskProducer producer = new DeliveryRiskProducer(kafkaTemplate, Duration.ofSeconds(1));

        assertThatCode(() -> producer.sendDeliveryRiskEvent(event)).doesNotThrowAnyException();
    }

    @Test
    void throwsWhenTheSendCompletesExceptionally() {
        DeliveryRiskEvent event = new DeliveryRiskEvent(2L, "Seoul", 0);
        CompletableFuture<SendResult<String, DeliveryRiskEvent>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("broker unavailable"));
        when(kafkaTemplate.send(anyString(), anyString(), eq(event))).thenReturn(future);

        DeliveryRiskProducer producer = new DeliveryRiskProducer(kafkaTemplate, Duration.ofSeconds(1));

        assertThatThrownBy(() -> producer.sendDeliveryRiskEvent(event))
                .isInstanceOf(DeliveryRiskPublishException.class);
    }

    @Test
    void throwsWhenTheSendNeverCompletesWithinTheTimeout() {
        DeliveryRiskEvent event = new DeliveryRiskEvent(3L, "Seoul", 0);
        CompletableFuture<SendResult<String, DeliveryRiskEvent>> neverCompletes = new CompletableFuture<>();
        when(kafkaTemplate.send(anyString(), anyString(), eq(event))).thenReturn(neverCompletes);

        DeliveryRiskProducer producer = new DeliveryRiskProducer(kafkaTemplate, Duration.ofMillis(200));

        assertThatThrownBy(() -> producer.sendDeliveryRiskEvent(event))
                .isInstanceOf(DeliveryRiskPublishException.class);
    }

    private SendResult<String, DeliveryRiskEvent> successfulSendResult() {
        ProducerRecord<String, DeliveryRiskEvent> producerRecord =
                new ProducerRecord<>("delivery-risk-events", "1", new DeliveryRiskEvent(1L, "Seoul", 0));
        RecordMetadata metadata =
                new RecordMetadata(new TopicPartition("delivery-risk-events", 0), 0, 0, 0, 0, 0);
        return new SendResult<>(producerRecord, metadata);
    }
}
