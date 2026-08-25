CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    parent_id BIGINT,
    status VARCHAR(255) NOT NULL DEFAULT 'ACTIVE',

    CONSTRAINT fk_category_parent
        FOREIGN KEY (parent_id)
        REFERENCES categories (id)
);