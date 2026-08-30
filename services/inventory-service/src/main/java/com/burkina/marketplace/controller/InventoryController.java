package com.burkina.marketplace.controller;

import com.burkina.marketplace.domain.entity.Inventory;
import com.burkina.marketplace.dto.request.UpdateQuantityRequest;
import com.burkina.marketplace.dto.response.InventoryResponse;
import com.burkina.marketplace.mapper.InventoryMapper;
import com.burkina.marketplace.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryMapper inventoryMapper;
    private final InventoryService inventoryService;

    @PatchMapping("/{productId}/inventory")
    public ResponseEntity<InventoryResponse> updateProductQuantity(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateQuantityRequest request
    ) {
        Long userId = Long.valueOf(jwt.getSubject());

        Inventory inventory = inventoryService.updateQuantity(userId, productId, request);

        return ResponseEntity.ok().body(inventoryMapper.toResponse(inventory));
    }

    @GetMapping("/{productId}/inventory")
    public ResponseEntity<InventoryResponse> getInventory(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId
    ) {
        Long userId = Long.valueOf(jwt.getSubject());

        Inventory inventory = inventoryService.getInventoryByUserIdAndProductId(userId, productId);

        return ResponseEntity.ok().body(inventoryMapper.toResponse(inventory));
    }
}
