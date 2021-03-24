CREATE TABLE condition(
    id SERIAL PRIMARY KEY,
    uuid VARCHAR UNIQUE NOT NULL,
    candidate_min_acceptable NUMERIC NOT NULL,
    currency VARCHAR(16) NOT NULL,
    gross_or_net VARCHAR(8) NOT NULL,
    annual_or_monthly VARCHAR(8) NOT NULL,
    extra_comments VARCHAR(64),
    ts TIMESTAMP NOT NULL
);
