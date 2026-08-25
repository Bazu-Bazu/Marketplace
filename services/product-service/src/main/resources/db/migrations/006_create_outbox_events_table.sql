CREATE TABLE outbox_events (
        id BIGSERIAL PRIMARY KEY,
        topic VARCHAR(255) NOT NULL,
        event_type VARCHAR(50) NOT NULL,
        status VARCHAR(20) NOT NULL DEFAULT 'NEW'
            CHECK (status IN ('NEW', 'PROCESSING', 'SENT')),
        payload TEXT NOT NULL,
        created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_outbox_status_created
    ON outbox_events(status, created_at);

CREATE INDEX idx_outbox_event_type
    ON outbox_events(event_type);