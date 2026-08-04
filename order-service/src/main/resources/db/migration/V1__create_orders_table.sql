CREATE TABLE orders (
    id BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    address VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
