package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.Product;
import com.burkina.marketplace.service.event.ProductEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminProductService {

    private final ProductQueryService productQueryService;
    private final ProductEventPublisher productEventPublisher;

    @Transactional
    public void lockProduct(Long productId) {
        Product product = productQueryService.getProductById(productId);

        boolean locked = product.lock();

        if (locked) {
            productEventPublisher.publishProductLocked(product);
        }
    }

    @Transactional
    public void unlockProduct(Long productId) {
        Product product = productQueryService.getProductById(productId);

        boolean unlocked = product.unlock();

        if (unlocked) {
            productEventPublisher.publishProductUnlocked(product);
        }
    }
}
