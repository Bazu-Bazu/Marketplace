CREATE TABLE inventories (
     id BIGSERIAL PRIMARY KEY,
     product_id BIGINT NOT NULL UNIQUE,
     seller_id BIGINT NOT NULL,
     status VARCHAR(32) NOT NULL,
     quantity INTEGER NOT NULL DEFAULT 0,
     reserved_quantity INTEGER NOT NULL DEFAULT 0
);