package com.burkina.marketplace.dto.data;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductData(
        String name,
        String description,
        BigDecimal price
) {}
