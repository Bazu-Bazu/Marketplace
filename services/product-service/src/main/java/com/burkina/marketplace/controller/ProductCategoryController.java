package com.burkina.marketplace.controller;

import com.burkina.marketplace.domain.entity.ProductCategory;
import com.burkina.marketplace.dto.request.AddProductCategoryRequest;
import com.burkina.marketplace.dto.response.ProductCategoryResponse;
import com.burkina.marketplace.mapper.ProductCategoryMapper;
import com.burkina.marketplace.service.ProductCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryMapper productCategoryMapper;
    private final ProductCategoryService productCategoryService;

    @PostMapping("/{productId}/categories")
    public ResponseEntity<ProductCategoryResponse> addCategoryToProduct(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId,
            @Valid @RequestBody AddProductCategoryRequest request
    ) {
        Long userId = Long.valueOf(jwt.getSubject());

        ProductCategory productCategory = productCategoryService.addProductCategory(userId, productId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(productCategoryMapper.toResponse(productCategory));
    }

    @DeleteMapping("/{productId}/categories/{categoryId}")
    public ResponseEntity<Void> removeCategoryFromProduct(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId,
            @PathVariable Long categoryId
    ) {
        Long userId = Long.valueOf(jwt.getSubject());

        productCategoryService.removeProductCategory(userId, productId, categoryId);

        return ResponseEntity.noContent().build();
    }
}
