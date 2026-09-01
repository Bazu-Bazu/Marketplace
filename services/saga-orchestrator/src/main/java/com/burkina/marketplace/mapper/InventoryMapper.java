package com.burkina.marketplace.mapper;

import com.burkina.marketplace.dto.data.ValidatedCart;
import com.burkina.marketplace.dto.request.ReserveItemRequest;
import com.burkina.marketplace.dto.response.ReserveResponse;
import lombok.RequiredArgsConstructor;
import marketplace.inventory.Inventory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InventoryMapper {

    public Inventory.ReserveRequest toReserveRequest(Long sagaId, List<ReserveItemRequest> reserveItems) {
        return Inventory.ReserveRequest.newBuilder()
                    .setSagaId(sagaId)
                    .addAllItems(reserveItems.stream()
                            .map(this::toReserveItem)
                            .toList())
                    .build();
    }

    private Inventory.ReserveItem toReserveItem(ReserveItemRequest request) {
        return Inventory.ReserveItem.newBuilder()
                .setProductId(request.productId())
                .setQuantity(request.quantity())
                .build();
    }

    public ReserveResponse toReserveResponse(Inventory.ReserveResponse response) {
        ReserveResponse.ReservationStatus status = switch (response.getStatus()) {
            case RESERVED -> ReserveResponse.ReservationStatus.RESERVED;
            case REJECTED -> ReserveResponse.ReservationStatus.REJECTED;
            default -> throw new IllegalArgumentException("Unknown status: " + response.getStatus());
        };

        return ReserveResponse.builder()
                .reservationId(response.getReservationId())
                .status(status)
                .build();
    }

    public Inventory.ReleaseRequest toReleaseRequest(Long reservationId) {
        return Inventory.ReleaseRequest.newBuilder()
                    .setReservationId(reservationId)
                    .build();
    }

    public List<ReserveItemRequest> toReserveItemsRequest(List<ValidatedCart.ValidatedCartItem> items) {
        return items.stream()
                .map(this::toReserveItemRequest)
                .toList();
    }

    private ReserveItemRequest toReserveItemRequest(ValidatedCart.ValidatedCartItem item) {
        return ReserveItemRequest.builder()
                .productId(item.productId())
                .quantity(item.quantity())
                .build();
    }
}
