package com.burkina.marketplace.dto.response;

import com.burkina.marketplace.domain.enums.InventoryStatus;
import lombok.Builder;

@Builder
public record InventoryResponse(
        Long id,
        Long productId,
        Long sellerId,
        InventoryStatus status,
        Integer quantity,
        Integer reservedQuantity
) {}
