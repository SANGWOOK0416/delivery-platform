-- Database-per-service: each service gets its own database on the shared
-- local Postgres instance. No schema or table is shared across services.
CREATE DATABASE order_db;
CREATE DATABASE notification_db;
