CREATE TABLE seller_phones (
    id BIGSERIAL PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    phone VARCHAR(255) NOT NULL UNIQUE,

    CONSTRAINT fk_seller_phone_seller FOREIGN KEY (seller_id)
        REFERENCES sellers(id) ON DELETE CASCADE
);