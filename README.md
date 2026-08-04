# Delivery Platform

An event-driven delivery workflow built with Spring Boot and Kafka.

```text
Order Service --order-events--> Weather Agent --delivery-risk-events--> Notification Service
```

## Local run

1. Start Kafka and Postgres with `docker compose up -d`. Postgres init creates two databases (`order_db`, `notification_db`) — one per service, per the database-per-service principle.
2. Set the required environment variables. Copy `.env.example` as a reference; Spring Boot does not load `.env` automatically.
3. Start each service with `./gradlew bootRun` from its module, or run the relevant Spring Boot application from your IDE. Each service runs its own Flyway migrations against its own database on startup.

`KAKAO_API_TOKEN` is required only when the notification service sends real Kakao messages. `KMA_SERVICE_KEY` is required only when weather-agent should look up live weather from the KMA 초단기실황(getUltraSrtNcst) API; without it, weather-agent falls back to a no-precipitation default. `KAKAO_REST_API_KEY` (a separate value from `KAKAO_API_TOKEN` — the app's REST API key, not a user OAuth token) lets weather-agent geocode the order's delivery address to a KMA grid cell via the Kakao Local API; without it, weather-agent falls back to a fixed grid coordinate (`KMA_GRID_NX`/`KMA_GRID_NY`, defaulting to a Seoul reference point). Never commit API keys, tokens, heap dumps, or local `.env` files.

## Event contracts

Event payloads and topic names live in `common-module`. This keeps Kafka producers and consumers on the same schema and avoids fragile `Map<String, Object>` parsing.

## Persistence

order-service persists each accepted order (`orders` table) and notification-service persists one row per delivery attempt (`notification_logs` table, including retries — a message that fails then succeeds leaves both rows). Each service owns its own Postgres database; neither reads the other's tables. Schema is managed with Flyway migrations under `src/main/resources/db/migration`.

## Known limitations / follow-up work

- **No transactional outbox.** order-service saves the order to Postgres and then publishes `OrderCreatedEvent` to Kafka as two separate steps, not one atomic operation. If the process crashes between the two, the order exists in the database but the event never fires. A full fix needs an outbox table plus a poller/CDC process (e.g. Debezium) publishing from it — deliberately left out of this pass to keep scope bounded.
- **order-service's Kafka publish is still fire-and-forget.** `OrderProducer.sendOrderCreatedEvent` does not wait for the broker acknowledgment before the API returns 202, so a publish failure after a successful DB save is not reflected in the HTTP response. (weather-agent's equivalent publish to `delivery-risk-events` was already made synchronous with a timeout — see `DeliveryRiskProducer` — as part of the DLQ work; the same treatment for order-service was deferred to avoid changing the order API's response latency/contract without a separate decision.)
