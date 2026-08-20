package com.burkina.marketplace.dto.data;

import lombok.Builder;

@Builder
public record SellerData(
        String name,
        String description,
        String avatarUrl,
        String inn,
        String address
) {}
