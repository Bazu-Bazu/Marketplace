CREATE TABLE product_medias (
    id BIGSERIAL PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    product_id BIGINT NOT NULL,
    sort_order INTEGER NOT NULL,

    CONSTRAINT fk_product_media_product
        FOREIGN KEY (product_id)
        REFERENCES products (id)
        ON DELETE CASCADE
);