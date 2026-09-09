package com.burkina.marketplace.dto.data;

import lombok.Builder;

import java.util.List;

@Builder
public record ReserveData(
        Long sagaId,
        List<ReserveItemData> items
) {}
