package com.burkina.marketplace.controller;

import com.burkina.marketplace.dto.request.AddProductCategoriesRequest;
import com.burkina.marketplace.dto.request.DeleteProductCategoriesRequest;
import com.burkina.marketplace.dto.response.ProductCategoriesResponse;
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

    @PostMapping("/{productId}/categories}")
    public ResponseEntity<ProductCategoriesResponse> addCategoriesToProduct(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId,
            @Valid @RequestBody AddProductCategoriesRequest request
    ) {
        Long userId = Long.valueOf(jwt.getSubject());

        var productCategories = productCategoryService.addProductCategories(userId, productId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(productCategoryMapper.toResponse(productCategories));
    }

    @DeleteMapping("/{productId}/categories}")
    public ResponseEntity<Void> removeCategoriesFromProduct(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId,
            @Valid @RequestBody DeleteProductCategoriesRequest request
    ) {
        Long userId = Long.valueOf(jwt.getSubject());

        productCategoryService.removeProductCategories(userId, productId, request);

        return ResponseEntity.noContent().build();
    }
}
