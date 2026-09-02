package com.burkina.marketplace.mapper;

import com.burkina.marketplace.domain.entity.Cart;
import com.burkina.marketplace.domain.entity.CartItem;
import com.burkina.marketplace.dto.response.CartResponse;
import org.springframework.stereotype.Component;

@Component
public class CartMapper {

    public CartResponse toResponse(Cart cart) {
        return CartResponse.from(cart);
    }

    public marketplace.cart.Cart.GetCartResponse toGetCartResponse(Cart cart) {
        return marketplace.cart.Cart.GetCartResponse.newBuilder()
                .setCartId(cart.getId())
                .addAllItems(cart.getItems().stream()
                        .map(this::cartItemResponse)
                        .toList())
                .build();
    }

    private marketplace.cart.Cart.CartItem cartItemResponse(CartItem item) {
        return marketplace.cart.Cart.CartItem.newBuilder()
                        .setProductId(item.getProductId())
                        .setQuantity(item.getQuantity())
                        .build();
    }
}
