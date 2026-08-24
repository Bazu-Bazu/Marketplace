package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.Product;
import com.burkina.marketplace.domain.repository.ProductRepository;
import com.burkina.marketplace.dto.request.UpdateProductRequest;
import com.burkina.marketplace.mapper.ProductMapper;
import com.burkina.marketplace.service.event.ProductEventPublisher;
import lombok.RequiredArgsConstructor;
import com.burkina.marketplace.dto.request.AddProductRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductCommandService {

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final ProductQueryService productQueryService;
    private final SellerAccessService sellerAccessService;
    private final ProductEventPublisher productEventPublisher;

    @Transactional
    public Product createProduct(Long userId, AddProductRequest request) {
        Long sellerId = sellerAccessService.getActiveSellerIdByUserId(userId);

        Product newProduct = Product.builder()
                .sellerId(sellerId)
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .build();

        return productRepository.save(newProduct);
    }

    @Transactional
    public Product updateProduct(Long userId, Long productId, UpdateProductRequest request) {
        Product product = productQueryService.getProductByUserIdAndProductId(userId, productId);

        product.update(productMapper.toData(request));

        productEventPublisher.publishProductUpdated(product);

        return product;
    }

    @Transactional
    public Product publishProduct(Long userId, Long productId) {
        Product product = productQueryService.getProductByUserIdAndProductId(userId, productId);

        boolean isPublished = product.publish();

        if (isPublished) {
            productEventPublisher.publishProductPublished(product);
        }

        return product;
    }

    @Transactional
    public Product recallProduct(Long userId, Long productId) {
        Product product = productQueryService.getProductByUserIdAndProductId(userId, productId);

        boolean isRecalled = product.recall();

        if (isRecalled) {
            productEventPublisher.publishProductRecalled(product);
        }

        return product;
    }
}
