package com.burkina.marketplace.dto.response;

import com.burkina.marketplace.domain.enums.ProductStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ProductWithDetailsResponse(
        Long id,
        Long sellerId,
        String name,
        String description,
        BigDecimal price,
        Boolean complete,
        ProductStatus status,
        List<ProductMediaResponse> medias,
        List<ProductCategoryResponse> categories
) {}
