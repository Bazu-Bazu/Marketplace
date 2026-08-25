CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    seller_id BIGINT NOT NULL,
    description VARCHAR(255) NOT NULL,
    price NUMERIC(19, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',

    CONSTRAINT chk_product_status
        CHECK (status IN ('CREATED', 'PUBLISHED', 'RECALLED', 'LOCKED'))
);