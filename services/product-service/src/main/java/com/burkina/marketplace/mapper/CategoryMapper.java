package com.burkina.marketplace.mapper;

import com.burkina.common.dto.event.CategoryActivatedEvent;
import com.burkina.common.dto.event.CategoryCreatedEvent;
import com.burkina.common.dto.event.CategoryInactivatedEvent;
import com.burkina.marketplace.domain.entity.Category;
import com.burkina.marketplace.dto.response.CategoryResponse;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .status(category.getStatus())
                .parentId(Optional.ofNullable(category.getParent())
                        .map(Category::getId)
                        .orElse(null))
                .build();
    }

    public CategoryCreatedEvent toCategoryCreatedEvent(Category category) {
        return CategoryCreatedEvent.builder()
                .categoryId(category.getId())
                .parentId(Optional.ofNullable(category.getParent())
                        .map(Category::getId)
                        .orElse(null))
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
