package com.marketplace.serviceOrder.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductEvent {

    private Long id;
    private Integer rating;

}
