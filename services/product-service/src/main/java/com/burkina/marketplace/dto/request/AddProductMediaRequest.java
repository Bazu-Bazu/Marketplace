package com.burkina.marketplace.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddProductMediaRequest(

        @NotBlank(message = "Media URL are required")
        String url,

        @NotNull(message = "Position is required")
        @Min(value = 0, message = "Position must be greater than or equal to 0")
        @Max(value = 15, message = "Position must be less than or equal to 15")
        Integer position
) {}
