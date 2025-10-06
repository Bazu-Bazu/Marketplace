package com.marketplace.serviceProduct.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class ProductDetailsResponse {

    private Long id;
    private String name;
    private String description;
    private Byte rating;
    private Integer price;
    private Integer count;
    private Long sellerId;
    private String sellerName;
    private Set<Long> categoryIds;
    private Set<String> categoryNames;
    private List<String> photoUrls;

}
