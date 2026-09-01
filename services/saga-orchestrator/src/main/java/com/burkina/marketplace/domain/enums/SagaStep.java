package com.burkina.marketplace.domain.enums;

public enum SagaStep {
    GET_CART,
    VALIDATE_PRODUCTS,
    RESERVE_INVENTORY,
    PAY,
    CREATE_ORDER,
    CLEAR_CART,
    COMPLETED
}