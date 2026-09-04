package com.burkina.marketplace.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemResponse(
        Long productId,
        Integer quantity,
        BigDecimal price
) {}
