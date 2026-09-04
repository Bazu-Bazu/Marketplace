package com.burkina.marketplace.dto.response;

import com.burkina.marketplace.domain.enums.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record OrderResponse(
        Long userId,
        List<OrderItemResponse> items,
        BigDecimal totalPrice,
        Long paymentId,
        OrderStatus status,
        Instant createdAt
) {}
