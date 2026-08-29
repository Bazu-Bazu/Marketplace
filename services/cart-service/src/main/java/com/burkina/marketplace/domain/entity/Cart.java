package com.burkina.marketplace.domain.entity;

import com.burkina.marketplace.exception.CartItemLimitExceededException;
import com.burkina.marketplace.exception.CartItemNotFoundException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "carts")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    private static final int MAX_DISTINCT_PRODUCTS = 100;

    @Transient
    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addItem(CartItem item) {
        if (items.size() >= MAX_DISTINCT_PRODUCTS) {
            throw new CartItemLimitExceededException(
                    String.format("Cart cannot contain more than %d different products", MAX_DISTINCT_PRODUCTS)
            );
        }

        item.setCart(this);
        items.add(item);
    }

    public void removeItem(Long cartItemId) {
        CartItem item = items.stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException(
                        String.format("Cart item %d not found in cart %d", cartItemId, id)
                ));

        items.remove(item);
    }

    public void empty() {
        items.clear();
    }

    public CartItem findItem(Long cartItemId) {
        return items.stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException(
                        String.format("Cart item with id %d is not in cart %d", cartItemId, id)
                ));
    }

    public CartItem findItemByProductId(Long productId) {
        return items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException(
                        String.format("Cart item with product id %d is not in cart %d", productId, id)
                ));
    }
}
