package com.burkina.marketplace.mapper;

import com.burkina.marketplace.domain.entity.Inventory;
import com.burkina.marketplace.dto.response.InventoryResponse;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryResponse toResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .sellerId(inventory.getSellerId())
                .status(inventory.getStatus())
                .quantity(inventory.getQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .build();
    }
}
