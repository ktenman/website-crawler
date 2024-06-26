CREATE TABLE IF NOT EXISTS transformed_data
(
    id        BIGSERIAL PRIMARY KEY,
    term      VARCHAR(255)  NOT NULL,
    incidence INT           NOT NULL DEFAULT 0,
    site      VARCHAR(8182) NOT NULL,
    timestamp TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP
);
