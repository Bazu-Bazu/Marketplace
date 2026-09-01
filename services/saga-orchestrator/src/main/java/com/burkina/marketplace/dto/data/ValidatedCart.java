package com.burkina.marketplace.dto.data;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ValidatedCart(
        Long cartId,
        List<ValidatedCartItem> items
) {

    @Builder
    public record ValidatedCartItem(
            Long productId,
            Integer quantity,
            BigDecimal price
    ) {}

    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(item -> item.price.multiply(BigDecimal.valueOf(item.quantity)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
