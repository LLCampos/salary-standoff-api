CREATE TABLE condition(
    id SERIAL PRIMARY KEY,
    uuid uuid UNIQUE,
    candidate_min_acceptable NUMERIC,
    compatibility_already_verified BOOLEAN,
    ts TIMESTAMP
);