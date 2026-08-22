package com.burkina.marketplace.domain.entity;

import com.burkina.marketplace.domain.enums.CategoryStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CategoryStatus status = CategoryStatus.ACTIVE;

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public boolean inactivate() {
        if (this.status == CategoryStatus.ACTIVE) {
            this.status = CategoryStatus.INACTIVE;

            return true;
        }

        return false;
    }

    public boolean activate() {
        if (this.status == CategoryStatus.INACTIVE) {
            this.status = CategoryStatus.ACTIVE;

            return true;
        }

        return false;
    }
}
