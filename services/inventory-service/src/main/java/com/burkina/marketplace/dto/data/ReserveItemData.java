package com.burkina.marketplace.dto.data;

import lombok.Builder;

@Builder
public record ReserveItemData(
        Long productId,
        Integer quantity
) {}
