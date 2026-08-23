package com.burkina.marketplace.mapper;

import com.burkina.marketplace.domain.entity.ProductCategory;
import com.burkina.marketplace.dto.response.ProductCategoriesResponse;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ProductCategoryMapper {

    public ProductCategoriesResponse toResponse(Collection<ProductCategory> productCategories) {
        return ProductCategoriesResponse.from(productCategories);
    }
}
