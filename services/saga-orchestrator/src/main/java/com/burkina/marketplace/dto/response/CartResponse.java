package com.burkina.marketplace.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record CartResponse(
        Long cartId,
        List<CartItemResponse> items
) {

    @Builder
    public record CartItemResponse(
            Long productId,
            Integer quantity
    ) {}
}
