CREATE TABLE search_categories (
    id BIGINT PRIMARY KEY,
    parent_id BIGINT,
    name VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);