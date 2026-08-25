package com.burkina.marketplace.controller;

import com.burkina.marketplace.domain.entity.Category;
import com.burkina.marketplace.dto.request.AddCategoryRequest;
import com.burkina.marketplace.dto.response.CategoryResponse;
import com.burkina.marketplace.mapper.CategoryMapper;
import com.burkina.marketplace.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final CategoryMapper categoryMapper;
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> addCategory(@Valid @RequestBody AddCategoryRequest request) {
        Category category = categoryService.addCategory(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryMapper.toResponse(category));
    }

    @PatchMapping("/{categoryId}/inactivate")
    public ResponseEntity<CategoryResponse> inactivateCategory(@PathVariable Long categoryId) {
        Category category = categoryService.inactivateCategory(categoryId);

        return ResponseEntity.ok().body(categoryMapper.toResponse(category));
    }

    @PatchMapping("/{categoryId}/activate")
    public ResponseEntity<CategoryResponse> activateCategory(@PathVariable Long categoryId) {
        Category category = categoryService.activateCategory(categoryId);

        return ResponseEntity.ok().body(categoryMapper.toResponse(category));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);

        return ResponseEntity.ok().body(categoryMapper.toResponse(category));
    }
}
