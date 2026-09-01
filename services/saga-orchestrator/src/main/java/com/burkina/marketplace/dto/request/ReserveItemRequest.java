package com.burkina.marketplace.dto.request;

import lombok.Builder;

@Builder
public record ReserveItemRequest(
        Long productId,
        Integer quantity
) {}
