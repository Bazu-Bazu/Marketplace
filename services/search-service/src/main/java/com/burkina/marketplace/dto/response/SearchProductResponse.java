package com.burkina.marketplace.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SearchProductResponse(
        Long productId,
        String name,
        BigDecimal price,
        Long sellerId,
        String description,
        String imageUrl
) {}
