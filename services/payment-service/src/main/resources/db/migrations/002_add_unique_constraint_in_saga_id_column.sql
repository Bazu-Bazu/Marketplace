ALTER TABLE payments
ADD CONSTRAINT uk_payments_saga_id UNIQUE (saga_id);