package com.portfolio.notification.consumer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.portfolio.common.event.DeliveryRiskEvent;
import com.portfolio.notification.service.KakaoMessageService;
import com.portfolio.notification.service.NotificationLogService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class NotificationConsumerTest {

    private final KakaoMessageService kakaoMessageService = Mockito.mock(KakaoMessageService.class);
    private final NotificationLogService notificationLogService = Mockito.mock(NotificationLogService.class);
    private final NotificationConsumer consumer =
            new NotificationConsumer(kakaoMessageService, notificationLogService);

    @Test
    void recordsSuccessWhenTheKakaoSendSucceeds() {
        DeliveryRiskEvent event = new DeliveryRiskEvent(1L, "Seoul", 0);

        consumer.consumeDeliveryRiskEvent(event);

        verify(notificationLogService).recordSuccess(event);
        verify(notificationLogService, Mockito.never()).recordFailure(any(), any());
    }

    @Test
    void recordsFailureAndRethrowsWhenTheKakaoSendFails() {
        DeliveryRiskEvent event = new DeliveryRiskEvent(2L, "Seoul", 1);
        RuntimeException sendFailure = new RuntimeException("401 Unauthorized");
        doThrow(sendFailure).when(kakaoMessageService).sendDeliveryAlert(event);

        assertThatThrownBy(() -> consumer.consumeDeliveryRiskEvent(event))
                .isSameAs(sendFailure);

        verify(notificationLogService).recordFailure(eq(event), eq("401 Unauthorized"));
    }
}
