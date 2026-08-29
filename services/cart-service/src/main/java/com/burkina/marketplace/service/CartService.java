package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.Cart;
import com.burkina.marketplace.domain.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    @Transactional
    public Cart getCartByUserIdWithItems(Long userId) {
        return cartRepository.findByUserIdWithItems(userId)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .userId(userId)
                                .build()));
    }

    @Transactional
    public void emptyCart(Long userId) {
        Cart cart = getCartByUserIdWithItems(userId);

        cart.empty();
    }
}
