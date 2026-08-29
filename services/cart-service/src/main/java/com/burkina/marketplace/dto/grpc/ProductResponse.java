package com.burkina.marketplace.dto.grpc;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductResponse(
        Long productId,
        BigDecimal price,
        boolean available
) {}
