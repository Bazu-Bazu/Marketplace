package com.burkina.marketplace.mapper;

import com.burkina.common.dto.event.marketplace.product.*;
import com.burkina.marketplace.domain.entity.Product;
import com.burkina.marketplace.domain.entity.ProductMedia;
import com.burkina.marketplace.domain.enums.ProductStatus;
import com.burkina.marketplace.dto.data.ProductData;
import com.burkina.marketplace.dto.request.UpdateProductRequest;
import com.burkina.marketplace.dto.response.ProductResponse;
import com.burkina.marketplace.dto.response.ProductWithDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final ProductMediaMapper productMediaMapper;
    private final ProductCategoryMapper productCategoryMapper;

    public ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .sellerId(product.getSellerId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .complete(product.complete())
                .build();
    }

    public ProductWithDetailsResponse toResponseWithDetails(Product product) {
        return ProductWithDetailsResponse.builder()
                .id(product.getId())
                .sellerId(product.getSellerId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .complete(product.complete())
                .medias(product.getMedias().stream()
                        .map(productMediaMapper::toResponse)
                        .toList())
                .categories(product.getCategories().stream()
                        .map(productCategoryMapper::toResponse)
                        .toList())
                .build();
    }

    public ProductData toData(UpdateProductRequest request) {
        return ProductData.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .build();
    }

    public ProductPublishedEvent toProductPublishedEvent(Product product) {
        return ProductPublishedEvent.builder()
                .productId(product.getId())
                .sellerId(product.getSellerId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getMedias().stream()
                        .filter(media -> media.getSortOrder() == 0)
                        .map(ProductMedia::getUrl)
                        .findFirst()
                        .orElse(null))
                .categoryIds(product.getCategories().stream()
                        .map(pc -> pc.getCategory().getId())
                        .toList())
                .build();
    }

    public ProductUpdatedEvent toProductUpdatedEvent(Product product) {
        return ProductUpdatedEvent.builder()
                .productId(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getMedias().stream()
                        .filter(media -> media.getSortOrder() == 0)
                        .map(ProductMedia::getUrl)
                        .findFirst()
                        .orElse(null))
                .categoryIds(product.getCategories().stream()
                        .map(pc -> pc.getCategory().getId())
                        .toList())
                .build();
    }

    public ProductLockedEvent toProductLockedEvent(Product product) {
        return ProductLockedEvent.builder()
                .productId(product.getId())
                .build();
    }

    public ProductUnlockedEvent toProductUnlockedEvent(Product product) {
        return ProductUnlockedEvent.builder()
                .productId(product.getId())
                .build();
    }

    public ProductRecalledEvent toProductRecalledEvent(Product product) {
        return ProductRecalledEvent.builder()
                .productId(product.getId())
                .build();
    }

    public marketplace.product.Product.GetProductForCartResponse toProductForCartResponse(Product product) {
        return marketplace.product.Product.GetProductForCartResponse.newBuilder()
                .setProductId(product.getId())
                .setPrice(product.getPrice().toPlainString())
                .setAvailable(product.getStatus() == ProductStatus.PUBLISHED)
                .build();
    }

    public marketplace.product.Product.ValidateProductsResponse toValidateProductsResponse(
            List<Long> productIds,
            List<Product> existingProducts
    ) {
        Map<Long, Product> productsById =
                existingProducts.stream()
                        .collect(Collectors.toMap(
                                com.burkina.marketplace.domain.entity.Product::getId,
                                Function.identity()
                        ));

        var response = marketplace.product.Product.ValidateProductsResponse.newBuilder();

        for (Long productId : productIds) {
            var product = productsById.get(productId);

            if (product == null) {
                response.addProducts(
                        marketplace.product.Product.ProductInfo.newBuilder()
                                .setProductId(productId)
                                .setExists(false)
                                .setAvailable(false)
                                .build()
                );

                continue;
            }

            response.addProducts(
                    marketplace.product.Product.ProductInfo.newBuilder()
                            .setProductId(product.getId())
                            .setPrice(product.getPrice().toPlainString())
                            .setExists(true)
                            .setAvailable(product.isActive())
                            .build()
            );
        }

        return response.build();
    }
}
