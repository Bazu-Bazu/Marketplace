package com.marketplace.serviceOrder.dto.grpc;

import lombok.Data;

import java.util.Map;

@Data
public class BasketValidationResult {

    private final Map<Long, BasketItemValidationResult> resultsByProductId;

    public BasketItemValidationResult getResultForItems(Long productId) {
        return resultsByProductId.get(productId);
    }

    public boolean hasErrors() {
        return resultsByProductId.values().stream()
                .anyMatch(result -> !result.isProductExist() ||
                                                          !result.isCountSufficient());
    }

}
