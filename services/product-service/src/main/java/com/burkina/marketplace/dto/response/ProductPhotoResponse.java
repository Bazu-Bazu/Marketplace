package com.marketplace.serviceProduct.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductPhotoResponse {

    private String id;
    private String name;
    private String url;
    private String contentType;
    private Long size;
    private Long productId;

}
