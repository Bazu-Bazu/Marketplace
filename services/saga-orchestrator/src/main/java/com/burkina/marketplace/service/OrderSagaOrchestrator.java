package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.OrderSaga;
import com.burkina.marketplace.domain.enums.SagaStep;
import com.burkina.marketplace.dto.data.ValidatedCart;
import com.burkina.marketplace.dto.response.*;
import com.burkina.marketplace.exception.*;
import com.burkina.marketplace.grpc.client.*;
import com.burkina.marketplace.mapper.InventoryMapper;
import com.burkina.marketplace.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderSagaOrchestrator {

    private final OrderMapper orderMapper;
    private final InventoryMapper inventoryMapper;

    private final OrderSagaService sagaService;
    private final OrderSagaCompensationService compensationService;

    private final CartGrpcClient cartGrpcClient;
    private final OrderGrpcClient orderGrpcClient;
    private final PaymentGrpcClient paymentGrpcClient;
    private final ProductGrpcClient productGrpcClient;
    private final InventoryGrpcClient inventoryGrpcClient;

    public OrderResponse createOrder(Long userId) {
        OrderSaga saga = sagaService.create(userId);

        try {
            sagaService.prepareStep(saga, SagaStep.GET_CART);

            CartResponse cart = processGetCart(saga);

            sagaService.prepareStep(saga, SagaStep.VALIDATE_PRODUCTS);

            ValidatedCart validatedCart = validateAndCalculateCart(cart);

            sagaService.prepareStep(saga, SagaStep.RESERVE_INVENTORY);

            processReserveInventory(saga, validatedCart);

            sagaService.prepareStep(saga, SagaStep.PAY);

            processPayment(saga, validatedCart);

            sagaService.prepareStep(saga, SagaStep.CREATE_ORDER);

            OrderResponse order = processCreateOrder(saga, validatedCart);

            sagaService.prepareStep(saga, SagaStep.CLEAR_CART);

            saga.complete();
            sagaService.save(saga);

            try {
                processClearCart(userId);
            } catch (RuntimeException e) {
                log.error("Failed to clear cart for user {}", userId);
            }

            return order;
        } catch (RuntimeException e) {
            handleFailure(saga, e);

            throw e;
        }
    }

    private CartResponse processGetCart(OrderSaga saga) {
        CartResponse cart = cartGrpcClient.getCart(saga.getUserId());

        if (cart.items().isEmpty()) {
            throw new EmptyCartException(
                    String.format("Cart of user %d is empty", saga.getUserId())
            );
        }

        return cart;
    }

    private ValidatedCart validateAndCalculateCart(CartResponse cart) {
        List<Long> productIds = cart.items().stream()
                .map(CartResponse.CartItemResponse::productId)
                .toList();

        List<ProductResponse> products = productGrpcClient.getProducts(productIds);

        Map<Long, ProductResponse> productsById = products.stream()
                .collect(Collectors.toMap(
                        ProductResponse::productId,
                        Function.identity()
                ));

        List<ValidatedCart.ValidatedCartItem> validatedItems = cart.items().stream()
                .map(item -> {
                    ProductResponse product = productsById.get(item.productId());

                    if (!product.exists()) {
                        throw new ProductNotFoundException(
                                String.format("Product %d not found", item.productId())
                        );
                    }

                    if (!product.available()) {
                        throw new ProductNotAvailableException(
                                String.format("Product %d is not available", item.productId())
                        );
                    }

                    return ValidatedCart.ValidatedCartItem.builder()
                            .productId(item.productId())
                            .quantity(item.quantity())
                            .price(product.actualPrice())
                            .build();
                })
                .toList();

        return ValidatedCart.builder()
                .cartId(cart.cartId())
                .items(validatedItems)
                .build();
    }

    private void processReserveInventory(OrderSaga saga, ValidatedCart cart) {
        List<ValidatedCart.ValidatedCartItem> items = cart.items();

        ReserveResponse response = inventoryGrpcClient.reserve(saga.getId(), inventoryMapper.toReserveItemsRequest(items));

        if (!response.isSuccess()) {
            throw new ReserveProductsException(
                    String.format("Failed to reserve products: %s", items.stream()
                            .map(ValidatedCart.ValidatedCartItem::productId)
                            .toList())
            );
        }

        saga.setReservationId(response.reservationId());
        sagaService.save(saga);
    }

    private void processPayment(OrderSaga saga, ValidatedCart cart) {
        PaymentResponse paymentResponse = paymentGrpcClient.pay(saga.getUserId(), saga.getId(), cart.getTotalPrice());

        if (!paymentResponse.isSuccess()) {
            throw new PaymentException(
                    String.format("Failed to process payment for saga %d", saga.getId())
            );
        }

        saga.setPaymentId(paymentResponse.paymentId());
        sagaService.save(saga);
    }

    private OrderResponse processCreateOrder(OrderSaga saga, ValidatedCart cart) {
        OrderResponse response = orderGrpcClient.createOrder(orderMapper.toCreateOrderRequest(saga, cart));

        saga.setOrderId(response.orderId());
        sagaService.save(saga);

        return response;
    }

    private void processClearCart(Long userId) {
        cartGrpcClient.clearCart(userId);
    }

    private void handleFailure(OrderSaga saga, RuntimeException originalException) {
        saga.startCompensation();
        sagaService.save(saga);

        try {
            compensationService.compensate(saga);

            saga.fail();
            sagaService.save(saga);
        } catch (RuntimeException compensationException) {
            throw new SagaCompensationFailedException(
                    String.format("Saga %d compensation failed", saga.getId())
            );
        }
    }
}
