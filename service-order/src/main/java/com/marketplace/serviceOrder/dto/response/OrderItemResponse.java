package com.marketplace.serviceOrder.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponse {

    private Long id;
    private Long productId;
    private Integer price;
    private Integer count;

}
