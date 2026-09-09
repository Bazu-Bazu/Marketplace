package com.burkina.marketplace.outbox.enums;

import lombok.Getter;

@Getter
public enum EventType {
    ORDER_CREATED("order-created");

    private final String topic;

    EventType(String topic) {
        this.topic = topic;
    }
}
