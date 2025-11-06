package com.marketplace.serviceOrder.dto.grpc;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BasketItemValidationResult {

    private boolean productExist;
    private int currentPrice;
    private int requestedCount;
    private int availableCount;
    private boolean countSufficient;

}
