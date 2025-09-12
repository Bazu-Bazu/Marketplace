package com.marketplace.serviceProduct.controller;

import com.marketplace.serviceProduct.dto.request.AddCategoryRequest;
import com.marketplace.serviceProduct.dto.response.CategoryResponse;
import com.marketplace.serviceProduct.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/add")
    public ResponseEntity<CategoryResponse> addCategory(@RequestBody AddCategoryRequest request) {
        CategoryResponse response = categoryService.addCategory(request);

        return ResponseEntity.ok(response);
    }

}
