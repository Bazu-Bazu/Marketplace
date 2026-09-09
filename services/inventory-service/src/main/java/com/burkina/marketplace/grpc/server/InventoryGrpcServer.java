package com.burkina.marketplace.grpc.server;

import com.burkina.marketplace.mapper.ReservationMapper;
import com.burkina.marketplace.service.ReservationService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import marketplace.inventory.Inventory;
import marketplace.inventory.InventoryServiceGrpc;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class InventoryGrpcServer extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final ReservationMapper reservationMapper;
    private final ReservationService reservationService;

    @Override
    public void reserve(Inventory.ReserveRequest request, StreamObserver<Inventory.ReserveResponse> responseObserver) {
        try {
            var reservation = reservationService.reserve(reservationMapper.toReserveData(request));

            responseObserver.onNext(reservationMapper.toReserveResponse(reservation, true));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void release(Inventory.ReleaseRequest request, StreamObserver<Inventory.ReleaseResponse> responseObserver) {
        try {
            var reservation = reservationService.release(request.getReservationId());

            responseObserver.onNext(reservationMapper.toReleaseResponse(reservation));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}
