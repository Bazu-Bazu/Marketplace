package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.Product;
import com.burkina.marketplace.domain.entity.ProductMedia;
import com.burkina.marketplace.domain.repository.ProductMediaRepository;
import com.burkina.marketplace.dto.request.AddProductMediaRequest;
import com.burkina.marketplace.service.event.ProductEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductMediaService {

    private final ProductQueryService productQueryService;
    private final ProductEventPublisher productEventPublisher;
    private final ProductMediaRepository productMediaRepository;

    @Transactional
    public ProductMedia addProductMedia(Long userId, Long productId, AddProductMediaRequest request) {
        Product product = productQueryService.getProductByUserIdAndProductIdWithDetails(userId, productId);

        ProductMedia newProductMedia = ProductMedia.builder()
                .url(request.url())
                .build();

        product.addMedia(newProductMedia, request.position());

        ProductMedia savedProductMedia = productMediaRepository.save(newProductMedia);

        if (savedProductMedia.getSortOrder() == 0) {
            productEventPublisher.publishProductUpdated(product);
        }

        return savedProductMedia;
    }

    @Transactional
    public void removeProductMedia(Long userId, Long productId, Long mediaId) {
        Product product = productQueryService.getProductByUserIdAndProductIdWithDetails(userId, productId);

        ProductMedia removedMedia = product.removeMedia(mediaId);

        if (removedMedia.getSortOrder() == 0) {
            productEventPublisher.publishProductUpdated(product);
        }

        if (!product.complete()) {
            boolean isRecalled = product.recall();

            if (isRecalled) {
                productEventPublisher.publishProductLocked(product);
            }
        }
    }
}
