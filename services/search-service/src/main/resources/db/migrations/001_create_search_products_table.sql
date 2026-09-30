CREATE TABLE search_products (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    seller_id BIGINT NOT NULL,
    price NUMERIC(19, 2) NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    image_url VARCHAR(500) NOT NULL
);