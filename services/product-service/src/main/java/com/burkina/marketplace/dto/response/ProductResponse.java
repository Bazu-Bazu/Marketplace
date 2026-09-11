package com.burkina.marketplace.dto.response;

import com.burkina.marketplace.domain.enums.ProductStatus;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductResponse(
        Long id,
        Long sellerId,
        String name,
        String description,
        BigDecimal price,
        Boolean complete,
        ProductStatus status
) {}
