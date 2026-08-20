package com.burkina.marketplace.dto.response;

import com.burkina.marketplace.domain.enums.SellerStatus;
import lombok.Builder;

import java.time.Instant;

@Builder
public record SellerShortResponse(
        Long id,
        Long userId,
        String name,
        String description,
        String avatarUrl,
        SellerStatus status,
        String inn,
        String address,
        Instant createdAt
) {}
