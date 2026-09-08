package com.burkina.marketplace.dto.response;

import lombok.Builder;

@Builder
public record PaymentResponse(
        Long paymentId
) {}
