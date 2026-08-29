package com.burkina.marketplace.dto.response;

import com.burkina.marketplace.domain.entity.Cart;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long id,
        Long userId,
        List<CartItemResponse> items,
        BigDecimal totalPrice
) {

    public static CartResponse from(Cart cart) {
        return new CartResponse(
                cart.getId(),
                cart.getUserId(),
                cart.getItems().stream()
                        .map(item -> new CartItemResponse(
                                item.getId(),
                                item.getProductId(),
                                item.getQuantity(),
                                item.getPrice()))
                        .toList(),
                cart.getTotalPrice());
    }
}
