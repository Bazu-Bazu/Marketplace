package com.marketplace.serviceProduct.dto.request;

import lombok.Data;

@Data
public class AddProductRequest {

    private String name;
    private String description;
    private Integer price;
    private Integer count;

}
