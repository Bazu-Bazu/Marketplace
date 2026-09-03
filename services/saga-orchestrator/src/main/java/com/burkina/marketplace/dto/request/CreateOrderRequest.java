package com.burkina.marketplace.dto.request;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record CreateOrderRequest(
        Long userId,
        List<OrderItem> items,
        BigDecimal totalPrice,
        Long paymentId
) {

    @Builder
    public record OrderItem(
            Long productId,
            Integer quantity,
            BigDecimal price
    ) {}
}

