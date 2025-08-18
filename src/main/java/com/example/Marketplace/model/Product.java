package com.example.Marketplace.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer count;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @Transient
    private List<ProductPhoto> photos;

}
