package com.burkina.marketplace.dto.response;

import lombok.Builder;

@Builder
public record ReserveResponse(
        Long reservationId
) {}
