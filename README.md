# Delivery Platform

An event-driven delivery workflow built with Spring Boot and Kafka.

```text
Order Service --order-events--> Weather Agent --delivery-risk-events--> Notification Service
```

## Local run

1. Start Kafka with `docker compose up -d`.
2. Set the required environment variables. Copy `.env.example` as a reference; Spring Boot does not load `.env` automatically.
3. Start each service with `./gradlew bootRun` from its module, or run the relevant Spring Boot application from your IDE.

`KAKAO_API_TOKEN` is required only when the notification service sends real Kakao messages. `KMA_SERVICE_KEY` is required only when weather-agent should look up live weather from the KMA 초단기실황(getUltraSrtNcst) API; without it, weather-agent falls back to a no-precipitation default. `KAKAO_REST_API_KEY` (a separate value from `KAKAO_API_TOKEN` — the app's REST API key, not a user OAuth token) lets weather-agent geocode the order's delivery address to a KMA grid cell via the Kakao Local API; without it, weather-agent falls back to a fixed grid coordinate (`KMA_GRID_NX`/`KMA_GRID_NY`, defaulting to a Seoul reference point). Never commit API keys, tokens, heap dumps, or local `.env` files.

## Event contracts

Event payloads and topic names live in `common-module`. This keeps Kafka producers and consumers on the same schema and avoids fragile `Map<String, Object>` parsing.
