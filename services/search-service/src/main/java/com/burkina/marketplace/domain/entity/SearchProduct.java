package com.burkina.marketplace.domain.entity;

import com.burkina.marketplace.dto.data.ProductData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "search_products")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchProduct {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Long sellerId;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    @Builder.Default
    private Boolean available = true;

    @Column(nullable = false)
    private String imageUrl;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<SearchProductCategory> categories = new HashSet<>();

    public void addCategory(SearchProductCategory productCategory) {
        if (categories.stream()
                .anyMatch(pc -> pc.getCategory().getId()
                        .equals(productCategory.getCategory().getId()))) {
            return;
        }

        productCategory.setProduct(this);
        categories.add(productCategory);
    }

    public void removeCategory(SearchProductCategory productCategory) {
        categories.remove(productCategory);
    }

    public void lock() {
        this.available = false;
    }

    public void unlock() {
        this.available = true;
    }

    public void recall() {
        this.available = false;
    }

    public void update(ProductData data) {
        if (!Objects.equals(this.name, data.name())) {
            this.name = data.name();
        }
        if (!Objects.equals(this.description, data.description())) {
            this.description = data.description();
        }
        if (!Objects.equals(this.price, data.price())) {
            this.price = data.price();
        }
        if (!Objects.equals(this.imageUrl, data.imageUrl())) {
            this.imageUrl = data.imageUrl();
        }
    }
}
