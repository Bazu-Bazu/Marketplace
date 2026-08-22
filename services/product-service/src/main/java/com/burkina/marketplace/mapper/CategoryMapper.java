package com.burkina.marketplace.mapper;

import com.burkina.marketplace.domain.entity.Category;
import com.burkina.marketplace.dto.response.CategoryResponse;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        CategoryResponse response = CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .status(category.getStatus())
                .build();

        if (category.getParent() != null) {
               response.setParentId(category.getParent().getId());
        }

        return response;
    }
}
