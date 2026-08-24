package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.Product;
import com.burkina.marketplace.domain.repository.ProductRepository;
import com.burkina.marketplace.exception.AuthorizationException;
import com.burkina.marketplace.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductQueryService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Product getProductByUserIdAndProductId(Long userId, Long productId) {
        return productRepository.findUpdatableProduct(userId, productId)
                .orElseThrow(() -> new AuthorizationException(
                        String.format("User %d is not authorized to access product %d", userId, productId)
                ));
    }

    @Transactional(readOnly = true)
    public Product getProductByUserIdAndProductIdWithDetails(Long userId, Long productId) {
        return productRepository.findUpdatableProductWithDetails(userId, productId)
                .orElseThrow(() -> new AuthorizationException(
                        String.format("User %d is not authorized to access product %d", userId, productId)
                ));
    }

    @Transactional(readOnly = true)
    public Product getProductByIdWithDetails(Long productId) {
        return productRepository.findByIdWithDetails(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("Product %d not found", productId)
                ));
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("Product %d not found", productId)
                ));
    }

    @Transactional(readOnly = true)
    public Page<Product> getProductsByUserId(Long userId, Pageable pageable) {
        return productRepository.findAllByUserId(userId, pageable);
    }
}
