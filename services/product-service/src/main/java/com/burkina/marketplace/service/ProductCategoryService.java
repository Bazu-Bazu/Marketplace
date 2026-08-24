package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.Category;
import com.burkina.marketplace.domain.entity.Product;
import com.burkina.marketplace.domain.entity.ProductCategory;
import com.burkina.marketplace.domain.repository.ProductCategoryRepository;
import com.burkina.marketplace.dto.request.AddProductCategoryRequest;
import com.burkina.marketplace.service.event.ProductEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final CategoryService categoryService;
    private final ProductQueryService productQueryService;
    private final ProductEventPublisher productEventPublisher;
    private final ProductCategoryRepository productCategoryRepository;

    @Transactional
    public ProductCategory addProductCategory(Long userId, Long productId, AddProductCategoryRequest request) {
        Product product = productQueryService.getProductByUserIdAndProductIdWithDetails(userId, productId);

        Category category = categoryService.getActiveCategoryById(request.categoryId());

        ProductCategory productCategory = ProductCategory.builder()
                .category(category)
                .build();

        product.addCategory(productCategory);

        ProductCategory savedProductCategory = productCategoryRepository.save(productCategory);

        productEventPublisher.publishCategoriesAddedToProduct(product);

        return savedProductCategory;
    }

    @Transactional
    public void removeProductCategory(Long userId, Long productId, Long productCategoryId) {
        Product product = productQueryService.getProductByUserIdAndProductIdWithDetails(userId, productId);

        product.removeCategory(productCategoryId);

        productEventPublisher.publishCategoriesRemovedFromProduct(product);

        if (!product.complete()) {
            boolean isRecalled = product.recall();

            if (isRecalled) {
                productEventPublisher.publishProductLocked(product);
            }
        }
    }
}
