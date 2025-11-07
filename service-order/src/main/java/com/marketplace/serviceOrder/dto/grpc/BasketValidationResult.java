package com.marketplace.serviceOrder.dto.grpc;

import lombok.Data;

import java.util.Map;

@Data
public class BasketValidationResult {

    private final Map<Long, BasketItemValidationResult> resultsByProductId;

    public BasketItemValidationResult getResultForItem(Long productId) {
        return resultsByProductId.get(productId);
    }

    public boolean hasNotExistItems() {
        return resultsByProductId.values().stream()
                .anyMatch(result -> !result.isProductExist());
    }

    public boolean hasNotCountSufficientItems() {
        return resultsByProductId.values().stream()
                .anyMatch(result -> !result.isCountSufficient());
    }

}
