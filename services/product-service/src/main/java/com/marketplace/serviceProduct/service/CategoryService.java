package com.marketplace.serviceProduct.service;

import com.marketplace.serviceProduct.dto.request.AddCategoryRequest;
import com.marketplace.serviceProduct.dto.response.CategoryResponse;
import com.marketplace.serviceProduct.entity.Category;
import com.marketplace.serviceProduct.exception.CategoryException;
import lombok.RequiredArgsConstructor;
import com.marketplace.serviceProduct.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse addCategory(AddCategoryRequest request) {
        Optional<Category> category = categoryRepository.findByName(request.getName().toLowerCase());
        if (category.isPresent()) {
            throw new CategoryException("Category already exists.");
        }

        Category newCategory = new Category();
        newCategory.setName(request.getName().toLowerCase());

        Long parentId = request.getParentId();
        if (parentId != null) {
            Category parentCategory = findCategoryById(parentId);
            newCategory.setParent(parentCategory);
        }
        categoryRepository.save(newCategory);

        return buildCategoryResponse(newCategory);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = findCategoryById(categoryId);

        categoryRepository.delete(category);
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException("Category not found."));
    }

    private CategoryResponse buildCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .build();
    }

}
