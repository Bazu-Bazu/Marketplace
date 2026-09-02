package com.burkina.marketplace.domain.entity;

import com.burkina.marketplace.domain.enums.ProductStatus;
import com.burkina.marketplace.dto.data.ProductData;
import com.burkina.marketplace.exception.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "products")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long sellerId;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.CREATED;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<ProductCategory> categories = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<ProductMedia> medias = new HashSet<>();

    private static final int MAX_MEDIA = 15;
    private static final int MAX_CATEGORIES = 10;

    public boolean publish() {
        if (!complete()) {
            throw new ProductCanNotBePublished(
                    String.format("Product %d is not complete", id)
            );
        }

        if (status != ProductStatus.PUBLISHED) {
            status = ProductStatus.PUBLISHED;

            return true;
        }

        return false;
    }

    public boolean recall() {
        if (status != ProductStatus.RECALLED) {
            status = ProductStatus.RECALLED;

            return true;
        }

        return false;
    }

    public boolean lock() {
        if (status != ProductStatus.LOCKED) {
            status = ProductStatus.LOCKED;

            return true;
        }

        return false;
    }

    public boolean unlock() {
        if (status == ProductStatus.LOCKED) {
            status = ProductStatus.PUBLISHED;

            return true;
        }

        return false;
    }

    public boolean isActive() {
        return status == ProductStatus.PUBLISHED;
    }

    public boolean complete() {
        return !categories.isEmpty() && !medias.isEmpty();
    }

    public void addCategory(ProductCategory productCategory) {
        if (categories.size() >= MAX_CATEGORIES) {
            throw new ProductCategoryLimitExceededException(
                    String.format("Product cannot have more than %d categories", MAX_CATEGORIES)
            );
        }

        if (categories.stream()
                .anyMatch(pc -> pc.getCategory().getId()
                        .equals(productCategory.getCategory().getId()))) {
            throw new ProductCategoryAlreadyExistsException(
                    String.format("Category %d is already assigned to product %d", productCategory.getCategory().getId(), id)
            );
        }

        productCategory.setProduct(this);
        categories.add(productCategory);
    }

    public void removeCategory(Long productCategoryId) {
        ProductCategory productCategory = categories.stream()
                .filter(pc -> pc.getId().equals(productCategoryId))
                .findFirst()
                .orElseThrow(() -> new ProductCategoryNotFoundException(
                        String.format("Category with id %d does not belong to product %d", productCategoryId, id)
                ));

        categories.remove(productCategory);
    }

    public void addMedia(ProductMedia media, int position) {
        if (medias.size() >= MAX_MEDIA) {
            throw new ProductMediaLimitExceededException(
                    String.format("Product cannot have more than %d medias", MAX_MEDIA)
            );
        }

        if (position < 0 || position > medias.size()) {
            throw new IllegalMediaPositionException("Invalid media position");
        }

        if (medias.stream().anyMatch(m -> m.getUrl().equals(media.getUrl()))) {
            throw new ProductMediaAlreadyExistsException(
                    String.format("Product %d already contains media %s", id, media.getUrl())
            );
        }

        shiftMediaPositionsRight(position);

        media.setProduct(this);
        media.setSortOrder(position);

        this.medias.add(media);
    }

    private void shiftMediaPositionsRight(int position) {
        medias.stream()
                .filter(m -> m.getSortOrder() >= position)
                .forEach(m -> m.setSortOrder(m.getSortOrder() + 1));
    }

    public ProductMedia removeMedia(Long mediaId) {
        ProductMedia media = medias.stream()
                .filter(m -> m.getId().equals(mediaId))
                .findFirst()
                .orElseThrow(() -> new ProductMediaNotFoundException(
                        String.format("Media with id %d does not belong to product %d", mediaId, id)
                ));

        medias.remove(media);

        shiftMediaPositionsLeft(media.getSortOrder());

        if (medias.isEmpty()) {
            status = ProductStatus.LOCKED;
        }

        return media;
    }

    private void shiftMediaPositionsLeft(int position) {
        medias.stream()
                .filter(m -> m.getSortOrder() > position)
                .forEach(m -> m.setSortOrder(m.getSortOrder() - 1));
    }

    public void update(ProductData data) {
        Optional.ofNullable(data.name())
                .ifPresent(value -> this.name = value);

        Optional.ofNullable(data.description())
                .ifPresent(value -> this.description = value);

        Optional.ofNullable(data.price())
                .ifPresent(value -> this.price = value);
    }
}
