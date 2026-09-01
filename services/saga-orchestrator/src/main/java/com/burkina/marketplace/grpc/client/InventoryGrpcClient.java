package com.burkina.marketplace.grpc.client;

import com.burkina.marketplace.dto.request.ReserveItemRequest;
import com.burkina.marketplace.dto.response.ReserveResponse;
import com.burkina.marketplace.exception.InventoryServiceUnavailableException;
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
            throw new InventoryServiceUnavailableException(
                    String.format("Inventory service is unavailable: %s", e.getMessage())
            );
        }
    }

    public void release(Long reservationId) {
        Inventory.ReleaseRequest request = inventoryMapper.toReleaseRequest(reservationId);

        try {
            inventoryServiceStub.release(request);
        } catch (StatusRuntimeException e) {
            throw new InventoryServiceUnavailableException(
                    String.format("Inventory service is unavailable: %s", e.getMessage())
            );
        }
    }
}
