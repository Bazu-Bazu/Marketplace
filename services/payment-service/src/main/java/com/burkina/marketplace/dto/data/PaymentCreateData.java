package com.burkina.marketplace.dto.data;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentCreateData(
        Long userId,
        Long sagaId,
        BigDecimal amount
) {}
