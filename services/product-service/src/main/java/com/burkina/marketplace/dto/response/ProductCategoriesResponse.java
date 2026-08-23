package com.burkina.marketplace.dto.response;

import com.burkina.marketplace.domain.entity.ProductCategory;

import java.util.Collection;
import java.util.List;

public record ProductCategoriesResponse(
        List<ProductCategoryResponse> responses
) {

    private record ProductCategoryResponse(
            Long id,
            Long categoryId,
            String categoryName
    ) {}

    public static ProductCategoriesResponse from(Collection<ProductCategory> categories) {
        return new ProductCategoriesResponse(mapCategories(categories));
    }

    private static List<ProductCategoryResponse> mapCategories(Collection<ProductCategory> categories) {
        return categories.stream()
                .map(category -> new ProductCategoryResponse(
                        category.getId(),
                        category.getCategory().getId(),
                        category.getCategory().getName()
                ))
                .toList();
    }
}
