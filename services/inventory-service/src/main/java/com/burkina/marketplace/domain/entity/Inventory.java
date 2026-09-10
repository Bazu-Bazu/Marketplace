package com.burkina.marketplace.domain.entity;

import com.burkina.marketplace.domain.enums.InventoryStatus;
import com.burkina.marketplace.exception.InsufficientStockException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventories")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long productId;

    @Column(nullable = false)
    private Long sellerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InventoryStatus status = InventoryStatus.ACTIVE;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer reservedQuantity = 0;

    public void activate() {
        status = InventoryStatus.ACTIVE;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void reserve(Integer amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        if (getAvailableQuantity() < amount) {
            throw new InsufficientStockException("Not enough available quantity");
        }

        reservedQuantity += amount;
    }

    public Integer getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    public void release(Integer amount) {
        if (amount <= 0) {
            return;
        }

        reservedQuantity -= amount;

        if (reservedQuantity < 0) {
            reservedQuantity = 0;
        }
    }

    public void confirm(Integer amount) {
        if (amount <= 0) {
            return;
        }

        if (reservedQuantity < amount) {
            throw new IllegalStateException("Cannot confirm more than reserved quantity");
        }

        quantity -= amount;
        reservedQuantity -= amount;
    }
}
