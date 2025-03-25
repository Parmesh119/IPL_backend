CREATE TABLE users (
    id TEXT PRIMARY KEY,
    username TEXT NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    created_at BIGINT
);