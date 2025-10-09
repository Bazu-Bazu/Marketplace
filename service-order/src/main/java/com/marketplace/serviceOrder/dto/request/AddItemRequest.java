package com.marketplace.serviceOrder.dto.request;

import lombok.Data;

@Data
public class AddItemRequest {

    private Long productId;
    private Integer count;

}
