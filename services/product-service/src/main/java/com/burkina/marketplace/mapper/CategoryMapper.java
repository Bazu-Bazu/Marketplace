package com.burkina.marketplace.mapper;

import com.burkina.common.dto.event.CategoryActivatedEvent;
import com.burkina.common.dto.event.CategoryCreatedEvent;
import com.burkina.common.dto.event.CategoryInactivatedEvent;
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

    public CategoryCreatedEvent toCategoryCreatedEvent(Category category) {
        return CategoryCreatedEvent.builder()
                .categoryId(category.getId())
                .parentId(category.getParent().getId())
                .name(category.getName())
                .build();
    }

    public CategoryInactivatedEvent toCategoryInactivatedEvent(Category category) {
        return CategoryInactivatedEvent.builder()
                .categoryId(category.getId())
                .build();
    }

    public CategoryActivatedEvent toCategoryActivatedEvent(Category category) {
        return CategoryActivatedEvent.builder()
                .categoryId(category.getId())
                .build();
    }
}
