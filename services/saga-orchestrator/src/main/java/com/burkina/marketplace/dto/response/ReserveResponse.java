package com.burkina.marketplace.dto.response;

import lombok.Builder;

@Builder
public record ReserveResponse(
        Long reservationId,
        ReservationStatus status
) {

    public enum ReservationStatus {
        RESERVED,
        REJECTED
    }

    public boolean isSuccess() {
        return status == ReservationStatus.RESERVED;
    }
}
