CREATE TABLE product_category (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,

    CONSTRAINT fk_product_category_product
        FOREIGN KEY (product_id)
        REFERENCES products (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_product_category_category
        FOREIGN KEY (category_id)
        REFERENCES categories (id),

    CONSTRAINT uk_product_category
        UNIQUE (product_id, category_id)
);