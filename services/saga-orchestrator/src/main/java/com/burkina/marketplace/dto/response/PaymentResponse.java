package com.burkina.marketplace.dto.response;

import lombok.Builder;

@Builder
public record PaymentResponse(
        Long paymentId,
        PaymentStatus status
) {

    public enum PaymentStatus {
        SUCCESS,
        FAILED
    }

    public boolean isSuccess() {
        return status == PaymentStatus.SUCCESS;
    }
}
