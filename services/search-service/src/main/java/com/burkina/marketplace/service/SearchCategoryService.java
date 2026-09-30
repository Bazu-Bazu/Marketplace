package com.burkina.marketplace.service;

import com.burkina.common.dto.event.marketplace.category.CategoryActivatedEvent;
import com.burkina.common.dto.event.marketplace.category.CategoryCreatedEvent;
import com.burkina.common.dto.event.marketplace.category.CategoryInactivatedEvent;
import com.burkina.marketplace.domain.entity.SearchCategory;
import com.burkina.marketplace.domain.repository.SearchCategoryRepository;
import com.burkina.marketplace.exception.SearchCategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchCategoryService {

    private final SearchCategoryRepository categoryRepository;

    @Transactional
    public void createCategory(CategoryCreatedEvent event) {
        SearchCategory category = SearchCategory.builder()
                .id(event.categoryId())
                .name(event.name())
                .build();

        if (event.parentId() != null) {
            SearchCategory parentCategory = getCategoryById(event.parentId());
            category.setParent(parentCategory);
        }

        categoryRepository.save(category);
    }

    @Transactional
    public void inactivateCategory(CategoryInactivatedEvent event) {
        SearchCategory category = getCategoryById(event.categoryId());

        category.inactivate();
    }

    @Transactional
    public void activateCategory(CategoryActivatedEvent event) {
        SearchCategory category = getCategoryById(event.categoryId());

        category.activate();
    }

    @Transactional(readOnly = true)
    public SearchCategory getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new SearchCategoryNotFoundException(
                        String.format("Category with id %d not found", categoryId)
                ));
    }

    @Transactional(readOnly = true)
    public Map<Long, SearchCategory> getCategoriesById(List<Long> categoryIds) {
        List<SearchCategory> categories = categoryRepository.findAllById(categoryIds);

        if (categories.size() != categoryIds.size()) {
            throw new SearchCategoryNotFoundException(
                    String.format("Some categories with ids %s not found", categoryIds)
            );
        }

        return categories.stream()
                .collect(Collectors.toMap(
                        SearchCategory::getId,
                        Function.identity()
                ));
    }
}
