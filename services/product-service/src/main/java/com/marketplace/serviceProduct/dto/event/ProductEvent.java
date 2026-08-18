package com.marketplace.serviceProduct.dto.event;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductEvent {

    private Long id;
    private Integer rating;

}
