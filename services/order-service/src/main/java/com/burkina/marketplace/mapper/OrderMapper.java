package com.burkina.marketplace.mapper;

import com.burkina.marketplace.dto.data.CreateOrderData;
import com.burkina.marketplace.dto.data.OrderItemData;
import marketplace.order.Order;
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
}
