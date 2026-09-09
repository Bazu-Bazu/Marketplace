package com.burkina.marketplace.mapper;

import com.burkina.marketplace.domain.entity.Reservation;
import com.burkina.marketplace.dto.data.ReserveData;
import com.burkina.marketplace.dto.data.ReserveItemData;
import marketplace.inventory.Inventory;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public ReserveData toReserveData(Inventory.ReserveRequest request) {
        return ReserveData.builder()
                .sagaId(request.getSagaId())
                .items(request.getItemsList().stream()
                        .map(this::toReserveItemData)
                        .toList())
                .build();
    }

    private ReserveItemData toReserveItemData(Inventory.ReserveItem item) {
        return ReserveItemData.builder()
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .build();
    }

    public Inventory.ReserveResponse toReserveResponse(Reservation reservation, boolean success) {
        return Inventory.ReserveResponse.newBuilder()
                .setReservationId(reservation.getId())
                .build();
    }

    public Inventory.ReleaseResponse toReleaseResponse(Reservation reservation) {
        return Inventory.ReleaseResponse.newBuilder()
                .build();
    }
}
