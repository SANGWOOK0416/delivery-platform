package com.portfolio.notification.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.portfolio.common.event.DeliveryRiskEvent;
import org.junit.jupiter.api.Test;

class StubKakaoMessageSenderTest {

    @Test
    void neverThrowsWhenFailureRateIsZero() {
        StubKakaoMessageSender sender = new StubKakaoMessageSender(0.0, 0L);
        DeliveryRiskEvent event = new DeliveryRiskEvent(1L, "Seoul", 0);

        assertThatCode(() -> sender.sendDeliveryAlert(event)).doesNotThrowAnyException();
    }

    @Test
    void alwaysThrowsWhenFailureRateIsOne() {
        StubKakaoMessageSender sender = new StubKakaoMessageSender(1.0, 0L);
        DeliveryRiskEvent event = new DeliveryRiskEvent(2L, "Seoul", 0);

        assertThatThrownBy(() -> sender.sendDeliveryAlert(event))
                .isInstanceOf(StubKakaoSendException.class);
    }
}
