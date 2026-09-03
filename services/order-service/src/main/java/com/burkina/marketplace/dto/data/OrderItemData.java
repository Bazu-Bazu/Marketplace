package com.burkina.marketplace.dto.data;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemData(
        Long productId,
        Integer quantity,
        BigDecimal price
) {}