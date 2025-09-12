package com.marketplace.serviceProduct.service;

import com.marketplace.serviceProduct.dto.request.AddCategoryRequest;
import com.marketplace.serviceProduct.dto.response.CategoryResponse;
import com.marketplace.serviceProduct.entity.Category;
import com.marketplace.serviceProduct.exception.CategoryAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import com.marketplace.serviceProduct.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse addCategory(AddCategoryRequest request) {
        Optional<Category> category = categoryRepository.findByName(request.getName());
        if (category.isPresent()) {
            throw new CategoryAlreadyExistsException("Category already exists.");
        }

        Category newCategory = new Category();
        newCategory.setName(request.getName());
        Optional<Category> parentCategory = categoryRepository.findById(request.getParentId());
        if (parentCategory.isPresent()) {
            newCategory.setParent(parentCategory.get());
        }
        categoryRepository.save(newCategory);

        CategoryResponse response = CategoryResponse.builder()
                .name(newCategory.getName())
                .build();
        if (parentCategory.isPresent()) {
            response.setParentName(parentCategory.get().getName());
        }

        return response;
    }

}
