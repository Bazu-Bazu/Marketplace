package com.marketplace.serviceProduct.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class AddProductRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private Integer price;

    @NotBlank
    private Integer count;

    @NotEmpty
    private Set<Long> categoryIds;

    @NotBlank
    private List<String> photoUrls;

}
