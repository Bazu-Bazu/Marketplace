package com.marketplace.serviceOrder.dto.request;

import lombok.Data;

@Data
public class SetOrderItemRatingRequest {

    private Long orderItemId;
    private Integer rating;

}
