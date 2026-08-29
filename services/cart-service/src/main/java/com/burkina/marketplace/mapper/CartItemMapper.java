package com.burkina.marketplace.mapper;

import com.burkina.marketplace.domain.entity.CartItem;
import com.burkina.marketplace.dto.response.CartItemResponse;
import org.springframework.stereotype.Component;

@Component
public class CartItemMapper {

    public CartItemResponse toResponse(CartItem cartItem) {
        return CartItemResponse.builder()
                    .id(cartItem.getId())
                    .productId(cartItem.getProductId())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .build();
    }
}
