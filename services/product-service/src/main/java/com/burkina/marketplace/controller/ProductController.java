package com.burkina.marketplace.controller;

import com.burkina.marketplace.domain.entity.Product;
import com.burkina.marketplace.dto.request.AddProductRequest;
import com.burkina.marketplace.dto.request.UpdateProductRequest;
import com.burkina.marketplace.dto.response.ProductResponse;
import com.burkina.marketplace.dto.response.ProductWithDetailsResponse;
import com.burkina.marketplace.mapper.ProductMapper;
import com.burkina.marketplace.service.ProductQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.burkina.marketplace.service.ProductCommandService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductMapper productMapper;
    private final ProductQueryService productQueryService;
    private final ProductCommandService productCommandService;

    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody AddProductRequest request
    ) {
        Long userId = Long.valueOf(jwt.getSubject());

        Product product = productCommandService.createProduct(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(productMapper.toResponse(product));
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long userId = Long.valueOf(jwt.getSubject());

        Page<Product> products = productQueryService.getProductsByUserId(userId, pageable);

        return ResponseEntity.ok().body(products.map(productMapper::toResponse));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductWithDetailsResponse> getProductWithDetails(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId
    ) {
        Long userId = Long.valueOf(jwt.getSubject());

        Product product = productQueryService.getProductByUserIdAndProductIdWithDetails(userId, productId);

        return ResponseEntity.ok().body(productMapper.toResponseWithDetails(product));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        Long userId = Long.valueOf(jwt.getSubject());

        Product product = productCommandService.updateProduct(userId, productId, request);

        return ResponseEntity.ok().body(productMapper.toResponse(product));
    }

    @PatchMapping("/{productId}/publish")
    public ResponseEntity<ProductResponse> publishProduct(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId
    ) {
        Long userId = Long.valueOf(jwt.getSubject());

        Product product = productCommandService.publishProduct(userId, productId);

        return ResponseEntity.ok().body(productMapper.toResponse(product));
    }

    @PatchMapping("/{productId}/recall")
    public ResponseEntity<ProductResponse> recallProduct(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId
    ) {
        Long userId = Long.valueOf(jwt.getSubject());

        Product product = productCommandService.recallProduct(userId, productId);

        return ResponseEntity.ok().body(productMapper.toResponse(product));
    }
}
