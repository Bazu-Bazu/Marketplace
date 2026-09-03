package com.burkina.marketplace.dto.data;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record CreateOrderData(
        Long userId,
        List<OrderItemData> items,
        BigDecimal totalPrice,
        Long paymentId
) {}