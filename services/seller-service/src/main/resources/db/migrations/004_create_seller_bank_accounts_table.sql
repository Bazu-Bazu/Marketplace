CREATE TABLE seller_bank_accounts (
    id BIGSERIAL PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    bank_name VARCHAR(255) NOT NULL,
    account_number VARCHAR(255) NOT NULL UNIQUE,

    CONSTRAINT fk_seller_bank_account_seller FOREIGN KEY (seller_id)
        REFERENCES sellers(id) ON DELETE CASCADE
);