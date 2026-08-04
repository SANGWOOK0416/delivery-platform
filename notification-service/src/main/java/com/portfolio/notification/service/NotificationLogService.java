package com.portfolio.notification.service;

import com.portfolio.common.event.DeliveryRiskEvent;
import com.portfolio.notification.dto.NotificationStatusResponse;
import com.portfolio.notification.entity.NotificationLogEntity;
import com.portfolio.notification.entity.NotificationStatus;
import com.portfolio.notification.repository.NotificationLogRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Records one row per delivery attempt, not per order — a message that fails twice before
 * succeeding on retry leaves three rows, which is exactly the audit trail we want.
 */
@Service
@RequiredArgsConstructor
public class NotificationLogService {

    private final NotificationLogRepository notificationLogRepository;
    private final NotificationEventBroadcaster notificationEventBroadcaster;

    public void recordSuccess(DeliveryRiskEvent event) {
        NotificationLogEntity saved = notificationLogRepository.save(new NotificationLogEntity(
                null,
                event.orderId(),
                event.address(),
                event.precipitationType(),
                NotificationStatus.SENT,
                null,
                Instant.now()
        ));
        notificationEventBroadcaster.broadcastStatusChange(NotificationStatusResponse.from(saved));
    }

    public void recordFailure(DeliveryRiskEvent event, String failureReason) {
        NotificationLogEntity saved = notificationLogRepository.save(new NotificationLogEntity(
                null,
                event.orderId(),
                event.address(),
                event.precipitationType(),
                NotificationStatus.FAILED,
                failureReason,
                Instant.now()
        ));
        notificationEventBroadcaster.broadcastStatusChange(NotificationStatusResponse.from(saved));
    }
}
