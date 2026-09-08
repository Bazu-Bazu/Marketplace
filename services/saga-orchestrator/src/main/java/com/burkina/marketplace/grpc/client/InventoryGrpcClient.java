package com.burkina.marketplace.grpc.client;

import com.burkina.marketplace.dto.request.ReserveItemRequest;
import com.burkina.marketplace.dto.response.ReserveResponse;
import com.burkina.marketplace.exception.InventoryServiceException;
import com.burkina.marketplace.exception.ReservationFailedException;
import com.burkina.marketplace.mapper.InventoryMapper;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import marketplace.inventory.Inventory;
import marketplace.inventory.InventoryServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InventoryGrpcClient {

    private final InventoryMapper inventoryMapper;

    @GrpcClient("inventory-service")
    private InventoryServiceGrpc.InventoryServiceBlockingStub inventoryServiceStub;

    public ReserveResponse reserve(Long sagaId, List<ReserveItemRequest> reserveItems) {
        Inventory.ReserveRequest request = inventoryMapper.toReserveRequest(sagaId, reserveItems);

        try {
            Inventory.ReserveResponse response = inventoryServiceStub.reserve(request);

            return inventoryMapper.toReserveResponse(response);
        } catch (StatusRuntimeException e) {
            throw switch (e.getStatus().getCode()) {
                case FAILED_PRECONDITION -> new ReservationFailedException(
                        String.format("Reservation failed for saga %d", sagaId)
                );

                default -> new InventoryServiceException(
                        String.format("Inventory service returned %s", e.getStatus())
                );
            };
        }
    }

    public void release(Long reservationId) {
        Inventory.ReleaseRequest request = inventoryMapper.toReleaseRequest(reservationId);

        try {
            inventoryServiceStub.release(request);
        } catch (StatusRuntimeException e) {
            throw new InventoryServiceException(
                    String.format("Inventory service returned %s", e.getMessage())
            );
        }
    }
}
