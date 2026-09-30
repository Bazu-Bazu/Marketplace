CREATE TABLE search_product_categories (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,

    CONSTRAINT fk_search_product_categories_product
        FOREIGN KEY (product_id)
        REFERENCES search_products (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_search_product_categories_category
        FOREIGN KEY (category_id)
        REFERENCES search_categories (id)
        ON DELETE CASCADE,

    CONSTRAINT uk_search_product_categories_product_category
        UNIQUE (product_id, category_id)
);