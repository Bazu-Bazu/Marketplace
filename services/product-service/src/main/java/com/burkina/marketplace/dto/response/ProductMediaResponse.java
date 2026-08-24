package com.burkina.marketplace.dto.response;

import lombok.Builder;

@Builder
public record ProductMediaResponse(
        Long id,
        String url
) {}
