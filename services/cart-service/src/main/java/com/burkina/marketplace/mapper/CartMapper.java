package com.burkina.marketplace.mapper;

import com.burkina.marketplace.domain.entity.Cart;
import com.burkina.marketplace.dto.response.CartResponse;
import org.springframework.stereotype.Component;

@Component
public class CartMapper {

    public CartResponse toResponse(Cart cart) {
        return CartResponse.from(cart);
    }
}
