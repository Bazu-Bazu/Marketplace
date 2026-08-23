package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.Product;
import com.burkina.marketplace.domain.entity.ProductCategory;
import com.burkina.marketplace.dto.request.AddProductCategoriesRequest;
import com.burkina.marketplace.dto.request.DeleteProductCategoriesRequest;
import com.burkina.marketplace.exception.ProductCategoryLimitExceededException;
import com.burkina.marketplace.exception.ProductCategoryNotFoundException;
import com.burkina.marketplace.service.event.ProductEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ProductEventPublisher productEventPublisher;

    private static final int MAX_CATEGORIES_PER_PRODUCT = 10;

    @Transactional
    public Set<ProductCategory> addProductCategories(Long userId, Long productId, AddProductCategoriesRequest request) {
        Product product = productService.getProductByUserIdAndProductIdWithDetails(userId, productId);

        var existingCategories = categoryService.getExistingNotAssignedToProduct(request.categoryIds(), productId);

        if (product.getCategories().size() + existingCategories.size() > MAX_CATEGORIES_PER_PRODUCT) {
            throw new ProductCategoryLimitExceededException(
                    String.format("Product cannot have more than %d categories", MAX_CATEGORIES_PER_PRODUCT)
            );
        }

        List<ProductCategory> newProductCategories = existingCategories.stream()
                .map(category -> {
                    return ProductCategory.builder()
                            .product(product)
                            .category(category)
                            .build();
                })
                .toList();

        product.addCategories(newProductCategories);

        productEventPublisher.publishCategoriesAddedToProduct(product);

        return product.getCategories();
    }

    @Transactional
    public void removeProductCategories(Long userId, Long productId, DeleteProductCategoriesRequest request) {
        Product product = productService.getProductByUserIdAndProductIdWithDetails(userId, productId);

        Set<Long> ids = request.productCategoryIds();
        Set<ProductCategory> productCategories = product.getCategories();
        Set<ProductCategory> toRemove = productCategories.stream()
                .filter(pc -> ids.contains(pc.getId()))
                .collect(Collectors.toSet());

        if (toRemove.size() != ids.size()) {
            throw new ProductCategoryNotFoundException(
                    String.format("Some categories in %s do not belong to product %d", ids, productId)
            );
        }

        productCategories.removeAll(toRemove);

        productEventPublisher.publishCategoriesRemovedFromProduct(product);
    }
}
