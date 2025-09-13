package com.marketplace.serviceOrder.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "baskets")
@Data
public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "basket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BasketItem> items;

    public void addItem(BasketItem item) {
        items.add(item);
        item.setBasket(this);
    }

    public void removeItem(BasketItem item) {
        items.remove(item);
        item.setBasket(null);
    }

}
