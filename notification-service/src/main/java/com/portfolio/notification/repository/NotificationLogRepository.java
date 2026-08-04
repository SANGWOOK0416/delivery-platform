package com.portfolio.notification.repository;

import com.portfolio.notification.entity.NotificationLogEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationLogRepository extends JpaRepository<NotificationLogEntity, Long> {

    @Query(value = "SELECT DISTINCT ON (order_id) * FROM notification_logs ORDER BY order_id, attempted_at DESC",
            nativeQuery = true)
    List<NotificationLogEntity> findLatestPerOrder();
}
