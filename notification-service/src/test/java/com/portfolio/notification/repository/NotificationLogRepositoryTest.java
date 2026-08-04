package com.portfolio.notification.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.portfolio.notification.entity.NotificationLogEntity;
import com.portfolio.notification.entity.NotificationStatus;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * Runs against the real Postgres started by docker-compose (see application.yml for the
 * connection details) rather than a Testcontainers-managed instance — this machine's Docker
 * Desktop returns a malformed response to Testcontainers' Docker client over the Windows
 * named pipe, so the automatically provisioned container never starts. Each test still gets
 * isolation from @DataJpaTest's default transactional rollback.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NotificationLogRepositoryTest {

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    @Test
    void persistsMultipleAttemptsForTheSameOrder() {
        notificationLogRepository.save(new NotificationLogEntity(
                null, 3001L, "Seoul", 1, NotificationStatus.FAILED, "401 Unauthorized", Instant.now()));
        notificationLogRepository.save(new NotificationLogEntity(
                null, 3001L, "Seoul", 1, NotificationStatus.SENT, null, Instant.now()));

        List<NotificationLogEntity> forThisOrder = notificationLogRepository.findAll().stream()
                .filter(log -> log.getOrderId().equals(3001L))
                .toList();

        assertThat(forThisOrder).hasSize(2);
        assertThat(forThisOrder).extracting(NotificationLogEntity::getStatus)
                .containsExactlyInAnyOrder(NotificationStatus.FAILED, NotificationStatus.SENT);
    }

    @Test
    void findLatestPerOrderReturnsOnlyTheMostRecentAttempt() {
        Instant earlier = Instant.now().minusSeconds(60);
        Instant later = Instant.now();
        notificationLogRepository.saveAndFlush(new NotificationLogEntity(
                null, 3002L, "Seoul", 1, NotificationStatus.FAILED, "401 Unauthorized", earlier));
        notificationLogRepository.saveAndFlush(new NotificationLogEntity(
                null, 3002L, "Seoul", 1, NotificationStatus.SENT, null, later));

        List<NotificationLogEntity> latest = notificationLogRepository.findLatestPerOrder().stream()
                .filter(log -> log.getOrderId().equals(3002L))
                .toList();

        assertThat(latest).hasSize(1);
        assertThat(latest.get(0).getStatus()).isEqualTo(NotificationStatus.SENT);
    }
}
