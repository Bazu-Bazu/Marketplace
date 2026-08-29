package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.Cart;
import com.burkina.marketplace.domain.entity.CartItem;
import com.burkina.marketplace.domain.repository.CartItemRepository;
import com.burkina.marketplace.dto.grpc.ProductResponse;
import com.burkina.marketplace.dto.request.AddCartItemRequest;
import com.burkina.marketplace.dto.request.UpdateQuantityRequest;
import com.burkina.marketplace.exception.ProductNotAvailableException;
import com.burkina.marketplace.grpc.client.ProductGrpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartService cartService;
    private final ProductGrpcClient productGrpcClient;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public CartItem addCartItem(Long userId, AddCartItemRequest request) {
        ProductResponse productResponse = productGrpcClient.getProductForCart(request.productId());

        if (!productResponse.available()) {
            throw new ProductNotAvailableException(
                    String.format("Product with id %d is not available", request.productId())
            );
        }

        Cart cart = cartService.getCartByUserIdWithItems(userId);

        CartItem existingItem = cart.findItemByProductId(productResponse.productId());

        if (existingItem != null) {
            existingItem.increaseQuantity(request.quantity());
            return existingItem;
        }

        CartItem newItem = CartItem.builder()
                .productId(productResponse.productId())
                .price(productResponse.price())
                .quantity(request.quantity())
                .build();

        cart.addItem(newItem);

        return cartItemRepository.save(newItem);
    }

    @Transactional
    public CartItem updateQuantity(Long userId, Long cartItemId, UpdateQuantityRequest request) {
        Cart cart = cartService.getCartByUserIdWithItems(userId);

        CartItem item = cart.findItem(cartItemId);

        item.setQuantity(request.quantity());

        return item;
    }

    @Transactional
    public void removeCartItem(Long userId, Long cartItemId) {
        Cart cart = cartService.getCartByUserIdWithItems(userId);

        cart.removeItem(cartItemId);
    }
}
