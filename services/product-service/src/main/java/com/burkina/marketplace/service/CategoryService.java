package com.burkina.marketplace.service;

import com.burkina.marketplace.dto.request.AddCategoryRequest;
import com.burkina.marketplace.domain.entity.Category;
import com.burkina.marketplace.exception.CategoryNotActiveException;
import com.burkina.marketplace.exception.CategoryNotFoundException;
import com.burkina.marketplace.service.event.CategoryEventPublisher;
import lombok.RequiredArgsConstructor;
import com.burkina.marketplace.domain.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryEventPublisher categoryEventPublisher;

    @Transactional
    public Category addCategory(AddCategoryRequest request) {
        Category newCategory = Category.builder()
                .name(request.name())
                .build();

        if (request.parentId() != null) {
            Category parent = getCategoryById(request.parentId());
            newCategory.setParent(parent);
        }

        Category savedCategory = categoryRepository.save(newCategory);

        categoryEventPublisher.publishCategoryCreated(savedCategory);

        return savedCategory;
    }

    @Transactional
    public Category inactivateCategory(Long categoryId) {
        Category category = getCategoryById(categoryId);
        boolean inactivated = category.inactivate();

        if (inactivated) {
            categoryEventPublisher.publishCategoryInactivated(category);
        }

        return category;
    }

    @Transactional
    public Category activateCategory(Long categoryId) {
        Category category = getCategoryById(categoryId);
        boolean activated = category.activate();

        if (activated) {
            categoryEventPublisher.publishCategoryActivated(category);
        }

        return category;
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findByIdWithParent(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(
                    String.format("Category with id %s not found", categoryId)
                ));
    }

    public Category getActiveCategoryById(Long categoryId) {
        Category category = getCategoryById(categoryId);

        if (!category.isActive()) {
            throw new CategoryNotActiveException(
                    String.format("Category with id %s is not active", categoryId)
            );
        }

        return category;
    }
}
