package com.burkina.marketplace.dto.request;

import com.burkina.marketplace.validation.annotation.UniqueCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record AddCategoryRequest(

        @NotBlank(message = "Name is required")
        @Pattern(regexp = "^[а-яА-Яa-zA-Z0-9 ]+$", message = "Name must contain only letters, digits or spaces")
        @Size(min = 2, max = 50, message = "Name must have 2-50 characters")
        @UniqueCategory
        String name,

        Long parentId
) {}
