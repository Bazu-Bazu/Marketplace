package com.burkina.marketplace.service;

import com.burkina.common.dto.event.marketplace.product.ProductLockedEvent;
import com.burkina.common.dto.event.marketplace.product.ProductPublishedEvent;
import com.burkina.common.dto.event.marketplace.product.ProductRecalledEvent;
import com.burkina.common.dto.event.marketplace.product.ProductUnlockedEvent;
import com.burkina.marketplace.domain.entity.Inventory;
import com.burkina.marketplace.domain.repository.InventoryRepository;
import com.burkina.marketplace.dto.request.UpdateQuantityRequest;
import com.burkina.marketplace.exception.AuthorizationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public void createInventory(ProductPublishedEvent event) {
        Optional<Inventory> inventory = inventoryRepository.findByProductId(event.productId());

        if (inventory.isPresent()) {
            inventory.get().activate();

            return;
        }

        Inventory newInventory = Inventory.builder()
                .productId(event.productId())
                .sellerId(event.sellerId())
                .build();

        inventoryRepository.save(newInventory);
    }

    @Transactional
    public Inventory updateQuantity(Long userId, Long productId, UpdateQuantityRequest request) {
        Inventory inventory = getInventoryByUserIdAndProductId(userId, productId);

        inventory.setQuantity(request.quantity());

        return inventory;
    }

    @Transactional(readOnly = true)
    public Inventory getInventoryByUserIdAndProductId(Long userId, Long productId) {
        return inventoryRepository.findUpdatableInventory(userId, productId)
                .orElseThrow(() -> new AuthorizationException(
                        String.format("User %d is not authorized to access product %d", userId, productId)
                ));
    }

    @Transactional
    public void inactivateInventory(ProductLockedEvent event) {
        inventoryRepository.inactivate(event.productId());
    }

    @Transactional
    public void inactivateInventory(ProductRecalledEvent event) {
        inventoryRepository.inactivate(event.productId());
    }

    @Transactional
    public void activateInventory(ProductUnlockedEvent event) {
        inventoryRepository.activate(event.productId());
    }
}
