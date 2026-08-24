package com.burkina.marketplace.dto.response;

import lombok.Builder;

@Builder
public record ProductCategoryResponse(
        Long id,
        Long categoryId,
        String categoryName
) {}
