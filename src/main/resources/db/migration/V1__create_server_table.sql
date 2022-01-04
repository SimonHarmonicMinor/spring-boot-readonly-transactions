CREATE TABLE server
(
    server_id BIGSERIAL PRIMARY KEY,
    name      VARCHAR(100) NOT NULL,
    switched  BOOLEAN      NOT NULL,
    type      VARCHAR(100) NOT NULL
);