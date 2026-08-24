package com.burkina.marketplace.controller;

import com.burkina.marketplace.domain.entity.Product;
import com.burkina.marketplace.dto.response.ProductWithDetailsResponse;
import com.burkina.marketplace.mapper.ProductMapper;
import com.burkina.marketplace.service.AdminProductService;
import com.burkina.marketplace.service.ProductQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final ProductMapper productMapper;
    private final ProductQueryService productQueryService;
    private final AdminProductService adminProductService;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductWithDetailsResponse> getProductWithDetails(@PathVariable Long productId) {
        Product product = productQueryService.getProductByIdWithDetails(productId);

        return ResponseEntity.ok().body(productMapper.toResponseWithDetails(product));
    }

    @PatchMapping("/{productId}/lock")
    public ResponseEntity<Void> lockProduct(@PathVariable Long productId) {
        adminProductService.lockProduct(productId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{productId}/unlock")
    public ResponseEntity<Void> unlockProduct(@PathVariable Long productId) {
        adminProductService.unlockProduct(productId);

        return ResponseEntity.noContent().build();
    }
}
