package com.burkina.marketplace.domain.repository;

import com.burkina.marketplace.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"categories", "categories.category", "medias"})
    @Query("""
            SELECT p
            FROM Product p
            JOIN SellerAccess sa
                ON sa.sellerId = p.sellerId
            WHERE p.id = :productId
                AND sa.userId = :userId
                AND sa.status = 'ACTIVE'
    """)
    Optional<Product> findUpdatableProductWithDetails(@Param("userId") Long userId, @Param("productId") Long productId);

    @Query("""
            SELECT p
            FROM Product p
            JOIN SellerAccess sa
                ON sa.sellerId = p.sellerId
            WHERE p.id = :productId
                AND sa.userId = :userId
                AND sa.status = 'ACTIVE'
    """)
    Optional<Product> findUpdatableProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    @EntityGraph(attributePaths = {"categories", "categories.category", "medias"})
    @Query("""
            SELECT p
            FROM Product p
            WHERE p.id = :productId
    """)
    Optional<Product> findByIdWithDetails(@Param("productId") Long productId);

    @Query("""
            SELECT p
            FROM Product p
            JOIN SellerAccess sa
                ON sa.sellerId = p.sellerId
            WHERE sa.userId = :userId
              AND sa.status = 'ACTIVE'
    """)
    Page<Product> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    List<Product> findAllById(Iterable<Long> ids);
}
