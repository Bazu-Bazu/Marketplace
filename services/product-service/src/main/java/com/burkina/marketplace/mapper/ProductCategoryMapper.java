package com.burkina.marketplace.mapper;

import com.burkina.marketplace.domain.entity.ProductCategory;
import com.burkina.marketplace.dto.response.ProductCategoryResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductCategoryMapper {

    public ProductCategoryResponse toResponse(ProductCategory productCategory) {
        return ProductCategoryResponse.builder()
                .id(productCategory.getId())
                .categoryId(productCategory.getCategory().getId())
                .categoryName(productCategory.getCategory().getName())
                .build();
    }
}
