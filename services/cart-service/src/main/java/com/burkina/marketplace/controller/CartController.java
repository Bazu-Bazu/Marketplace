package com.burkina.marketplace.controller;

import com.burkina.marketplace.domain.entity.Cart;
import com.burkina.marketplace.dto.response.CartResponse;
import com.burkina.marketplace.mapper.CartMapper;
import com.burkina.marketplace.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartMapper cartMapper;
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        Cart cart = cartService.getCartByUserIdWithItems(userId);

        return ResponseEntity.ok().body(cartMapper.toResponse(cart));
    }

    @DeleteMapping
    public ResponseEntity<Void> emptyCart(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        cartService.emptyCart(userId);

        return ResponseEntity.noContent().build();
    }
}
