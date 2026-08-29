CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    price NUMERIC(19, 2) NOT NULL,

    CONSTRAINT fk_cart_item_cart
        FOREIGN KEY (cart_id)
        REFERENCES carts(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_cart_product
        UNIQUE (cart_id, product_id),

    CONSTRAINT chk_cart_item_quantity
        CHECK (quantity > 0),

    CONSTRAINT chk_cart_item_price
        CHECK (price >= 0)
);