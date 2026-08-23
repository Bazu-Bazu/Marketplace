package com.burkina.marketplace.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;

@Builder
public record DeleteProductCategoriesRequest(

        @NotEmpty(message = "Category ids are required")
        @Size(max = 10, message = "A product can have at most 10 categories")
        Set<Long> productCategoryIds
) {}
