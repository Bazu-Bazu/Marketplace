package com.burkina.marketplace.mapper;

import com.burkina.marketplace.dto.response.CartResponse;
import marketplace.cart.Cart;
import org.springframework.stereotype.Component;

@Component
public class CartMapper {

    public Cart.GetCartRequest toGetCartRequest(Long userId) {
        return Cart.GetCartRequest.newBuilder()
                .setUserId(userId)
                .build();
    }

    public CartResponse toCartResponse(Cart.GetCartResponse response) {
        return CartResponse.builder()
                .cartId(response.getCartId())
                .items(response.getItemsList().stream()
                        .map(this::toCartItemResponse)
                        .toList())
                .build();
    }

    private CartResponse.CartItemResponse toCartItemResponse(Cart.CartItem cartItem) {
        return CartResponse.CartItemResponse.builder()
                .productId(cartItem.getProductId())
                .quantity(cartItem.getQuantity())
                .build();
    }

    public Cart.ClearCartRequest toClearCartRequest(Long userId) {
        return Cart.ClearCartRequest.newBuilder()
                .setUserId(userId)
                .build();
    }
}
