package com.burkina.marketplace.mapper;

import com.burkina.marketplace.domain.entity.OrderSaga;
import com.burkina.marketplace.dto.data.ValidatedCart;
import com.burkina.marketplace.dto.request.CreateOrderRequest;
import com.burkina.marketplace.dto.response.OrderResponse;
import marketplace.order.Order;
import org.springframework.stereotype.Component;


@Component
public class OrderMapper {

    public Order.CreateOrderRequest toCreateOrderRequest(CreateOrderRequest orderRequest) {
        return Order.CreateOrderRequest.newBuilder()
                    .setUserId(orderRequest.userId())
                    .addAllItems(orderRequest.items().stream()
                            .map(this::toOrderItem)
                            .toList())
                    .setTotalPrice(orderRequest.totalPrice().toString())
                    .setReservationId(orderRequest.reservationId())
                    .setPaymentId(orderRequest.paymentId())
                    .build();
    }

    private Order.OrderItem toOrderItem(CreateOrderRequest.OrderItem orderItem) {
        return Order.OrderItem.newBuilder()
                    .setProductId(orderItem.productId())
                    .setQuantity(orderItem.quantity())
                    .setPrice(orderItem.price().toString())
                    .build();
    }

    public OrderResponse toOrderResponse(Order.CreateOrderResponse response) {
        return OrderResponse.builder()
                    .orderId(response.getOrderId())
                    .build();
    }

    public Order.CancelOrderRequest toCancelOrderRequest(Long orderId) {
        return Order.CancelOrderRequest.newBuilder()
                    .setOrderId(orderId)
                    .build();
    }

    public CreateOrderRequest toCreateOrderRequest(OrderSaga saga, ValidatedCart cart) {
        return CreateOrderRequest.builder()
                    .userId(saga.getUserId())
                    .items(cart.items().stream()
                            .map(this::toOrderItem)
                            .toList())
                    .totalPrice(cart.getTotalPrice())
                    .reservationId(saga.getReservationId())
                    .paymentId(saga.getPaymentId())
                    .build();
    }

    private CreateOrderRequest.OrderItem toOrderItem(ValidatedCart.ValidatedCartItem item) {
        return CreateOrderRequest.OrderItem.builder()
                    .productId(item.productId())
                    .quantity(item.quantity())
                    .price(item.price())
                    .build();
    }
}
