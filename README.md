# Delivery Platform

An event-driven delivery workflow built with Spring Boot and Kafka.

```text
Order Service --order-events--> Weather Agent --delivery-risk-events--> Notification Service
```

## Local run

1. Start Kafka with `docker compose up -d`.
2. Set the required environment variables. Copy `.env.example` as a reference; Spring Boot does not load `.env` automatically.
3. Start each service with `./gradlew bootRun` from its module, or run the relevant Spring Boot application from your IDE.

`KAKAO_API_TOKEN` is required only when the notification service sends real Kakao messages. Never commit API keys, tokens, heap dumps, or local `.env` files.

## Event contracts

Event payloads and topic names live in `common-module`. This keeps Kafka producers and consumers on the same schema and avoids fragile `Map<String, Object>` parsing.
