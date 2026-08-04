package com.portfolio.notification.repository;

import com.portfolio.notification.entity.NotificationLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLogEntity, Long> {
}
