package com.portfolio.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.portfolio.notification.dto.NotificationStatusResponse;
import com.portfolio.notification.entity.NotificationLogEntity;
import com.portfolio.notification.entity.NotificationStatus;
import com.portfolio.notification.repository.NotificationLogRepository;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class NotificationQueryServiceTest {

    private final NotificationLogRepository notificationLogRepository = Mockito.mock(NotificationLogRepository.class);
    private final NotificationQueryService notificationQueryService =
            new NotificationQueryService(notificationLogRepository);

    @Test
    void mapsTheLatestPerOrderRowsToResponses() {
        NotificationLogEntity entity = new NotificationLogEntity(
                1L, 100L, "Seoul", 0, NotificationStatus.SENT, null, Instant.now());
        when(notificationLogRepository.findLatestPerOrder()).thenReturn(List.of(entity));

        List<NotificationStatusResponse> responses = notificationQueryService.findLatestStatusPerOrder();

        assertThat(responses).containsExactly(NotificationStatusResponse.from(entity));
    }
}
