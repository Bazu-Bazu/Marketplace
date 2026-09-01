package com.burkina.marketplace.dto.response;

import lombok.Builder;

@Builder
public record OrderResponse(
        Long orderId
) {}
