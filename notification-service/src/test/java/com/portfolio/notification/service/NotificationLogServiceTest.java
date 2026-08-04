package com.portfolio.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.portfolio.common.event.DeliveryRiskEvent;
import com.portfolio.notification.dto.NotificationStatusResponse;
import com.portfolio.notification.entity.NotificationLogEntity;
import com.portfolio.notification.entity.NotificationStatus;
import com.portfolio.notification.repository.NotificationLogRepository;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class NotificationLogServiceTest {

    private final NotificationLogRepository notificationLogRepository = Mockito.mock(NotificationLogRepository.class);
    private final NotificationEventBroadcaster notificationEventBroadcaster =
            Mockito.mock(NotificationEventBroadcaster.class);
    private final NotificationLogService notificationLogService =
            new NotificationLogService(notificationLogRepository, notificationEventBroadcaster);

    @Test
    void savesAndBroadcastsOnSuccess() {
        DeliveryRiskEvent event = new DeliveryRiskEvent(1L, "Seoul", 0);
        NotificationLogEntity saved = new NotificationLogEntity(
                1L, 1L, "Seoul", 0, NotificationStatus.SENT, null, Instant.now());
        when(notificationLogRepository.save(any(NotificationLogEntity.class))).thenReturn(saved);

        notificationLogService.recordSuccess(event);

        ArgumentCaptor<NotificationLogEntity> captor = ArgumentCaptor.forClass(NotificationLogEntity.class);
        verify(notificationLogRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(captor.getValue().getFailureReason()).isNull();

        verify(notificationEventBroadcaster).broadcastStatusChange(NotificationStatusResponse.from(saved));
    }

    @Test
    void savesAndBroadcastsOnFailure() {
        DeliveryRiskEvent event = new DeliveryRiskEvent(2L, "Seoul", 1);
        NotificationLogEntity saved = new NotificationLogEntity(
                2L, 2L, "Seoul", 1, NotificationStatus.FAILED, "401 Unauthorized", Instant.now());
        when(notificationLogRepository.save(any(NotificationLogEntity.class))).thenReturn(saved);

        notificationLogService.recordFailure(event, "401 Unauthorized");

        ArgumentCaptor<NotificationLogEntity> captor = ArgumentCaptor.forClass(NotificationLogEntity.class);
        verify(notificationLogRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(NotificationStatus.FAILED);
        assertThat(captor.getValue().getFailureReason()).isEqualTo("401 Unauthorized");

        verify(notificationEventBroadcaster).broadcastStatusChange(NotificationStatusResponse.from(saved));
    }
}
