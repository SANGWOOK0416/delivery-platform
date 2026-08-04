package com.portfolio.order.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.portfolio.order.entity.OrderEntity;
import java.time.Instant;
import java.util.Optional;
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
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void persistsAndReloadsAnOrder() {
        Instant now = Instant.now();
        orderRepository.save(new OrderEntity(2001L, 55L, "Seoul, Mapo-gu", now));

        Optional<OrderEntity> found = orderRepository.findById(2001L);

        assertThat(found).isPresent();
        assertThat(found.get().getCustomerId()).isEqualTo(55L);
        assertThat(found.get().getAddress()).isEqualTo("Seoul, Mapo-gu");
    }
}
