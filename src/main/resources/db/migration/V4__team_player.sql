ALTER TABLE players
    ADD COLUMN team_id TEXT REFERENCES team(id) ON DELETE SET NULL;
