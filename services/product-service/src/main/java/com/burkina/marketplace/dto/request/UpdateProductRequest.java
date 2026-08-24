package com.burkina.marketplace.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateProductRequest(

        @Size(min = 2, max = 50, message = "Name must have 2-50 characters")
        String name,

        @Size(max = 300, message = "Description must have before 300 characters")
        String description,

        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "Price must have at most 10 integer digits and 2 decimal places")
        BigDecimal price
) {}
