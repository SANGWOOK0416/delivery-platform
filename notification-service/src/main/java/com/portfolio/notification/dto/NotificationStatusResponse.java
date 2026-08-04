package com.portfolio.notification.dto;

import com.portfolio.notification.entity.NotificationLogEntity;
import java.time.Instant;

public record NotificationStatusResponse(
        Long orderId,
        String status,
        Integer precipitationType,
        String failureReason,
        Instant attemptedAt
) {

    public static NotificationStatusResponse from(NotificationLogEntity entity) {
        return new NotificationStatusResponse(
                entity.getOrderId(),
                entity.getStatus().name(),
                entity.getPrecipitationType(),
                entity.getFailureReason(),
                entity.getAttemptedAt()
        );
    }
}
