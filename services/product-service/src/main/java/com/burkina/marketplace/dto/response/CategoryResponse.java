package com.burkina.marketplace.dto.response;

import com.burkina.marketplace.domain.enums.CategoryStatus;
import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class CategoryResponse {
    private Long id;
    private String name;
    private Long parentId;
    private CategoryStatus status;
}
