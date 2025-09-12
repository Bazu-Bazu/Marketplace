package com.marketplace.serviceProduct.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCategoryRequest {

    @NotBlank
    private String name;

    private Long parentId;

}
