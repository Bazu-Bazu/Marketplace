package com.burkina.marketplace.controller;

import com.burkina.marketplace.domain.entity.CartItem;
import com.burkina.marketplace.dto.request.AddCartItemRequest;
import com.burkina.marketplace.dto.request.UpdateQuantityRequest;
import com.burkina.marketplace.dto.response.CartItemResponse;
import com.burkina.marketplace.mapper.CartItemMapper;
import com.burkina.marketplace.service.CartItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart/items")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemMapper cartItemMapper;
    private final CartItemService cartItemService;

    @PostMapping
    public ResponseEntity<CartItemResponse> addCartItem(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody AddCartItemRequest request
    ) {
        Long userId = Long.valueOf(jwt.getSubject());

        CartItem item = cartItemService.addCartItem(userId, request);

        return ResponseEntity.ok().body(cartItemMapper.toResponse(item));
    }

    @PatchMapping("/{cartItemId}")
    public ResponseEntity<CartItemResponse> updateQuantity(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateQuantityRequest request
    ) {
        Long userId = Long.valueOf(jwt.getSubject());

        CartItem item = cartItemService.updateQuantity(userId, cartItemId, request);

        return ResponseEntity.ok().body(cartItemMapper.toResponse(item));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeCartItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long cartItemId
    ) {
        Long userId = Long.valueOf(jwt.getSubject());

        cartItemService.removeCartItem(userId, cartItemId);

        return ResponseEntity.noContent().build();
    }
}
