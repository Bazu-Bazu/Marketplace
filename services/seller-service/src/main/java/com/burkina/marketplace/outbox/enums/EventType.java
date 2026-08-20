package com.burkina.marketplace.outbox.enums;

import lombok.Getter;

@Getter
public enum EventType {
    SELLER_REGISTERED("seller-registered"),
    SELLER_LOCKED("seller-locked"),
    SELLER_UNLOCKED("seller-unlocked"),
    SELLER_DELETED("seller-deleted");

    private final String topic;

    EventType(String topic) {
        this.topic = topic;
    }
}
