CREATE TABLE condition(
    id SERIAL PRIMARY KEY,
    uuid VARCHAR UNIQUE,
    candidate_min_acceptable NUMERIC,
    ts TIMESTAMP
);
