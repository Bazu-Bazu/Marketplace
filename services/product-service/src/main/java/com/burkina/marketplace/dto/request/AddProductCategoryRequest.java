package com.burkina.marketplace.dto.request;

import jakarta.validation.constraints.NotNull;

public record AddProductCategoryRequest(

        @NotNull(message = "Category id is required")
        Long categoryId
) {}
