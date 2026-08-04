CREATE TABLE notification_logs (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    address VARCHAR(255) NOT NULL,
    precipitation_type INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    failure_reason VARCHAR(1000),
    attempted_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_notification_logs_order_id ON notification_logs (order_id);
