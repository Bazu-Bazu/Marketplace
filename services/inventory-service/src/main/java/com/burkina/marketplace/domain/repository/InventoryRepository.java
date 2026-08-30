package com.burkina.marketplace.domain.repository;

import com.burkina.marketplace.domain.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(Long productId);

    @Modifying
    @Query("""
        UPDATE Inventory i
        SET i.status = 'INACTIVE'
        WHERE i.productId = :productId
    """)
    void inactivate(@Param("productId") Long productId);

    @Modifying
    @Query("""
        UPDATE Inventory i
        SET i.status = 'ACTIVE'
        WHERE i.productId = :productId
    """)
    void activate(@Param("productId") Long productId);

    @Query("""
            SELECT i
            FROM Inventory i
            JOIN SellerAccess sa
                ON sa.sellerId = i.sellerId
            WHERE i.productId = :productId
                AND sa.userId = :userId
                AND sa.status = 'ACTIVE'
    """)
    Optional<Inventory> findUpdatableInventory(@Param("userId") Long userId, @Param("productId") Long productId);
}
