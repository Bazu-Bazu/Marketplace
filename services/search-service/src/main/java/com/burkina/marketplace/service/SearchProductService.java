package com.burkina.marketplace.service;

import com.burkina.common.dto.event.marketplace.product.*;
import com.burkina.marketplace.domain.entity.SearchProduct;
import com.burkina.marketplace.domain.repository.SearchProductRepository;
import com.burkina.marketplace.exception.SearchProductNotFoundException;
import com.burkina.marketplace.mapper.SearchProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchProductService {

    private final SearchProductMapper productMapper;
    private final SearchProductRepository productRepository;
    private final SearchProductCategoryService productCategoryService;

    @Transactional
    public void createProduct(ProductPublishedEvent event) {
        SearchProduct product = SearchProduct.builder()
                .id(event.productId())
                .name(event.name())
                .description(event.description())
                .sellerId(event.sellerId())
                .price(event.price())
                .imageUrl(event.imageUrl())
                .build();

        productCategoryService.addProductCategories(product, event.categoryIds());

        productRepository.save(product);
    }

    @Transactional
    public void updateProduct(ProductUpdatedEvent event) {
        SearchProduct product = getProductById(event.productId());
        product.update(productMapper.toProductData(event));

        productCategoryService.syncProductCategories(product, event.categoryIds());
    }

    @Transactional
    public void lockProduct(ProductLockedEvent event) {
        SearchProduct product = getProductById(event.productId());

        product.lock();
    }

    @Transactional
    public void unlockProduct(ProductUnlockedEvent event) {
        SearchProduct product = getProductById(event.productId());

        product.unlock();
    }

    @Transactional
    public void recallProduct(ProductRecalledEvent event) {
        SearchProduct product = getProductById(event.productId());

        product.recall();
    }

    @Transactional
    public SearchProduct getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new SearchProductNotFoundException(
                        String.format("Product with id %d not found", productId)
                ));
    }
}
