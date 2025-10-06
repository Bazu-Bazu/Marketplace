package com.marketplace.serviceProduct.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductShortResponse {

    private Long id;
    private String name;
    private Byte rating;
    private Integer price;
    private Long sellerId;
    private String photoUrl;

}
