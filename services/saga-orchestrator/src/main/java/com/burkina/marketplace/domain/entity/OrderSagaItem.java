package com.burkina.marketplace.domain.entity;

public record OrderSagaItem(
        Long productId,
        Integer quantity
) {}
