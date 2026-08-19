CREATE TABLE seller_emails (
    id BIGSERIAL PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,

    CONSTRAINT fk_seller_email_seller FOREIGN KEY (seller_id)
        REFERENCES sellers(id) ON DELETE CASCADE
);