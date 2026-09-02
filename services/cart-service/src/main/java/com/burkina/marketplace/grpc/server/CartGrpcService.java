package com.burkina.marketplace.grpc.server;

import com.burkina.marketplace.mapper.CartMapper;
import com.burkina.marketplace.service.CartService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import marketplace.cart.Cart;
import marketplace.cart.CartServiceGrpc;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class CartGrpcService extends CartServiceGrpc.CartServiceImplBase {

    private final CartMapper cartMapper;
    private final CartService cartService;

    @Override
    public void getCart(
            Cart.GetCartRequest request,
            StreamObserver<Cart.GetCartResponse> responseObserver
    ) {
        try {
            var cart = cartService.getCartByUserIdWithItems(request.getUserId());

            responseObserver.onNext(cartMapper.toGetCartResponse(cart));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void clearCart(
            Cart.ClearCartRequest request,
            StreamObserver<Cart.ClearCartResponse> responseObserver
    ) {
        try {
            cartService.emptyCart(request.getUserId());

            responseObserver.onNext(Cart.ClearCartResponse.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}
