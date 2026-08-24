package com.burkina.marketplace.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_medias")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer sortOrder;
}
