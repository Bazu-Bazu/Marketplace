package com.burkina.marketplace.outbox.enums;

import lombok.Getter;

@Getter
public enum EventType {
    CATEGORY_CREATED("category-created"),
    CATEGORY_INACTIVATED("category-inactivated"),
    CATEGORY_ACTIVATED("category-activated"),

    PRODUCT_PUBLISHED("product-published"),
    PRODUCT_LOCKED("product-locked"),
    PRODUCT_UNLOCKED("product-unlocked"),
    PRODUCT_UPDATED("product-update"),
    PRODUCT_RECALLED("product-recalled");

    private final String topic;

    EventType(String topic) {
        this.topic = topic;
    }
}
