package com.burkina.marketplace.domain.repository;

import com.burkina.marketplace.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            SELECT p
            FROM Product p
            JOIN SellerAccess sa
                ON sa.sellerId = p.sellerId
            WHERE p.id = :productId
            AND sa.userId = :userId
            AND sa.status != 'DELETE'
    """)
    Optional<Product> findUpdatableProduct(@Param("userId") Long userId, @Param("productId") Long productId);
}
