package com.burkina.marketplace.service;

import com.burkina.common.dto.event.marketplace.order.OrderCreatedEvent;
import com.burkina.marketplace.domain.entity.Inventory;
import com.burkina.marketplace.domain.entity.Reservation;
import com.burkina.marketplace.domain.entity.ReservationItem;
import com.burkina.marketplace.domain.repository.ReservationRepository;
import com.burkina.marketplace.dto.data.ReserveData;
import com.burkina.marketplace.dto.data.ReserveItemData;
import com.burkina.marketplace.exception.InsufficientStockException;
import com.burkina.marketplace.exception.InventoryNotFoundException;
import com.burkina.marketplace.exception.ReservationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final InventoryService inventoryService;
    private final ReservationRepository reservationRepository;

    @Transactional
    public Reservation reserve(ReserveData reserveData) {
        Optional<Reservation> existingReservation = reservationRepository.findBySagaId(reserveData.sagaId());

        if (existingReservation.isPresent()) {
            return existingReservation.get();
        }

        List<Long> productIds = reserveData.items().stream()
                .map(ReserveItemData::productId)
                .toList();

        List<Inventory> inventories = inventoryService.getInventoriesByProductIdIn(productIds);

        Map<Long, Inventory> inventoryMap = inventories.stream()
                        .collect(Collectors.toMap(
                                Inventory::getProductId,
                                Function.identity()
                        ));

        checkOfPresence(reserveData.items(), inventoryMap);

        Reservation reservation = Reservation.builder()
                        .sagaId(reserveData.sagaId())
                        .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        List<ReservationItem> reservationItems = new ArrayList<>();

        for (ReserveItemData item : reserveData.items()) {
            Inventory inventory = inventoryMap.get(item.productId());

            inventory.reserve(item.quantity());

            ReservationItem reservationItem =
                    ReservationItem.builder()
                            .reservation(savedReservation)
                            .productId(item.productId())
                            .quantity(item.quantity())
                            .build();

            reservationItems.add(reservationItem);
        }

        reservation.addItems(reservationItems);

        return reservationRepository.save(reservation);
    }

    private void checkOfPresence(List<ReserveItemData> items, Map<Long, Inventory> inventoryMap) {
        for (ReserveItemData item : items) {
            Inventory inventory = inventoryMap.get(item.productId());

            if (inventory == null) {
                throw new InventoryNotFoundException("Inventory not found: " + item.productId());
            }

            if (inventory.getAvailableQuantity() < item.quantity()) {
                throw new InsufficientStockException("Not enough stock for product " + item.productId());
            }
        }
    }

    @Transactional
    public Reservation release(Long reservationId) {
        Reservation reservation = getReservationById(reservationId);

        List<ReservationItem> items = reservation.getItems();

        List<Long> productIds = items.stream()
                .map(ReservationItem::getProductId)
                .toList();

        List<Inventory> inventories = inventoryService.getInventoriesByProductIdIn(productIds);

        Map<Long, Inventory> inventoryMap = inventories.stream()
                .collect(Collectors.toMap(
                        Inventory::getProductId,
                        Function.identity()
                ));

        for (ReservationItem item : reservation.getItems()) {
            Inventory inventory = inventoryMap.get(item.getProductId());

            inventory.release(item.getQuantity());
        }

        reservation.release();

        return reservation;
    }

    @Transactional
    public void confirm(OrderCreatedEvent event) {
        Reservation reservation = getReservationById(event.reservationId());

        List<ReservationItem> items = reservation.getItems();

        List<Long> productIds = items.stream()
                .map(ReservationItem::getProductId)
                .toList();

        List<Inventory> inventories = inventoryService.getInventoriesByProductIdIn(productIds);

        Map<Long, Inventory> inventoryMap = inventories.stream()
                .collect(Collectors.toMap(
                        Inventory::getProductId,
                        Function.identity()
                ));

        for (ReservationItem item : items) {
            Inventory inventory = inventoryMap.get(item.getProductId());

            if (inventory == null) {
                throw new InventoryNotFoundException(
                        String.format("Inventory not found: %d", item.getProductId())
                );
            }

            inventory.confirm(item.getQuantity());
        }

        reservation.confirm();
    }

    @Transactional(readOnly = true)
    private Reservation getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(
                        String.format("Reservation not found: %d", reservationId)
                ));
    }
}
