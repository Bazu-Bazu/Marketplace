package com.burkina.marketplace.grpc.client;

import com.burkina.marketplace.dto.response.CartResponse;
import com.burkina.marketplace.exception.CartServiceException;
import com.burkina.marketplace.mapper.CartMapper;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import marketplace.cart.Cart;
import marketplace.cart.CartServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartGrpcClient {

    private final CartMapper cartMapper;

    @GrpcClient("cart-service")
    private CartServiceGrpc.CartServiceBlockingStub cartServiceStub;

    public CartResponse getCart(Long userId) {
        Cart.GetCartRequest request = cartMapper.toGetCartRequest(userId);

        try {
            Cart.GetCartResponse response = cartServiceStub.getCart(request);

            return cartMapper.toCartResponse(response);
        } catch (StatusRuntimeException e) {
            throw new CartServiceException(
                    String.format("Cart service returned %s", e.getMessage())
            );
        }
    }

    public void clearCart(Long userId) {
        Cart.ClearCartRequest request = cartMapper.toClearCartRequest(userId);

        try {
            cartServiceStub.clearCart(request);
        } catch (StatusRuntimeException e) {
            throw new CartServiceException(
                    String.format("Cart service returned %s", e.getMessage())
            );
        }
    }
}
