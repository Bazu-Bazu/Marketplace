CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'CREATED',
    total_price NUMERIC(19, 2) NOT NULL,
    payment_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_orders_user_id
    ON orders(user_id);

CREATE UNIQUE INDEX uk_orders_payment_id
    ON orders(payment_id);