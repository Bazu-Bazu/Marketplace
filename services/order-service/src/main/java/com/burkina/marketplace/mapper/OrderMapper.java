package com.burkina.marketplace.mapper;

import com.burkina.marketplace.domain.entity.OrderItem;
import com.burkina.marketplace.dto.data.CreateOrderData;
import com.burkina.marketplace.dto.data.OrderItemData;
import com.burkina.marketplace.dto.response.OrderItemResponse;
import com.burkina.marketplace.dto.response.OrderResponse;
import marketplace.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderMapper {

    public CreateOrderData toCreateOrderData(Order.CreateOrderRequest request) {
        return CreateOrderData.builder()
                .userId(request.getUserId())
                .items(request.getItemsList().stream()
                        .map(this::toOrderItemData)
                        .toList())
                .paymentId(request.getPaymentId())
                .totalPrice(new BigDecimal(request.getTotalPrice()))
                .build();
    }

    private OrderItemData toOrderItemData(Order.OrderItem item) {
        return OrderItemData.builder()
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .price(new BigDecimal(item.getPrice()))
                .build();
    }

    public Order.CreateOrderResponse toCreateOrderResponse(com.burkina.marketplace.domain.entity.Order order) {
        return Order.CreateOrderResponse.newBuilder()
                .setOrderId(order.getId())
                .build();
    }

    public Order.CancelOrderResponse toCancelOrderResponse(com.burkina.marketplace.domain.entity.Order order) {
        return Order.CancelOrderResponse.newBuilder()
                .build();
    }

    public Page<OrderResponse> toResponses(Page<com.burkina.marketplace.domain.entity.Order> orders) {
        return orders.map(this::toResponse);
    }

    private OrderResponse toResponse(com.burkina.marketplace.domain.entity.Order order) {
        return OrderResponse.builder()
                .userId(order.getUserId())
                .items(order.getItems().stream()
                        .map(this::toOrderItemResponse)
                        .toList())
                .totalPrice(order.getTotalPrice())
                .paymentId(order.getPaymentId())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private OrderItemResponse toOrderItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .build();
    }
}
