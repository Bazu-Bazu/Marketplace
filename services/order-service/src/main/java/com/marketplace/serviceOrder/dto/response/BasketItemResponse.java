package com.marketplace.serviceOrder.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BasketItemResponse {

    private Long itemId;
    private Long productId;
    private Integer count;
    private Long basketId;

}
