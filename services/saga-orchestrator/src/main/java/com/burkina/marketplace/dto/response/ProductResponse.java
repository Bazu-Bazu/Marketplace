package com.burkina.marketplace.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductResponse(
        Long productId,
        boolean available,
        boolean exists,
        BigDecimal actualPrice
) {}
