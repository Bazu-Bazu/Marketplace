package com.burkina.marketplace.dto.response;

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
        List<ProductMediaResponse> medias,
        List<ProductCategoryResponse> categories
) {}
