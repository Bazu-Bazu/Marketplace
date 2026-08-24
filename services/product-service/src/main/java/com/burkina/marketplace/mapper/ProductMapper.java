package com.burkina.marketplace.mapper;

import com.burkina.marketplace.domain.entity.Product;
import com.burkina.marketplace.dto.data.ProductData;
import com.burkina.marketplace.dto.request.UpdateProductRequest;
import com.burkina.marketplace.dto.response.ProductResponse;
import com.burkina.marketplace.dto.response.ProductWithDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
}
