package com.burkina.marketplace.dto.request;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PayRequest(
        Long userId,
        Long sagaId,
        BigDecimal amount
) {}
