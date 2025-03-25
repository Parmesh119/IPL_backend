CREATE TABLE Team (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    owner TEXT NOT NULL,
    coach TEXT NOT NULL,
    captain TEXT NOT NULL,
    vice_captain TEXT NOT NULL,
    created_at BIGINT,
    updated_at BIGINT
);