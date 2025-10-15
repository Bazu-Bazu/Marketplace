package com.marketplace.serviceOrder.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer count;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

}
