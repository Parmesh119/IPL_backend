CREATE TABLE players (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    country VARCHAR(50) NOT NULL,
    age INT,
    role VARCHAR(50) CHECK (role IN ('Batsman', 'Bowler', 'All-rounder', 'Wicketkeeper')),
    batting_style VARCHAR(50) CHECK (batting_style IN ('Right-handed', 'Left-handed')),
    bowling_style VARCHAR(50),
    created_at BIGINT,
    updated_at BIGINT
);