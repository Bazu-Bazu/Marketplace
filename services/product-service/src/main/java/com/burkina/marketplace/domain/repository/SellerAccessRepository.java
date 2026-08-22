package com.burkina.marketplace.domain.repository;

import com.burkina.marketplace.domain.entity.SellerAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerAccessRepository extends JpaRepository<SellerAccess, Long> {

    @Modifying
    @Query("""
        UPDATE SellerAccess sa
        SET sa.status = 'LOCKED'
        WHERE sa.sellerId = :sellerId
    """)
    void lockSeller(@Param("sellerId") Long sellerId);

    @Modifying
    @Query("""
        UPDATE SellerAccess sa
        SET sa.status = 'ACTIVE'
        WHERE sa.sellerId = :sellerId
    """)
    void unlockSeller(@Param("sellerId") Long sellerId);

    @Modifying
    @Query("""
        UPDATE SellerAccess sa
        SET sa.status = 'DELETED'
        WHERE sa.sellerId = :sellerId
    """)
    void deleteSeller(@Param("sellerId") Long sellerId);
}