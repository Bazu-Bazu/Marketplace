package com.marketplace.serviceProduct.dto.response;

import com.marketplace.serviceProduct.entity.Category;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class ProductResponse {

    Long id;
    String name;
    String description;
    Integer price;
    Integer count;
    Long sellerId;
    Set<Category> categories;
    List<String> photoUrls;

}
