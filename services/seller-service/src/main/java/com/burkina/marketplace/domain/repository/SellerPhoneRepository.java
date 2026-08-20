package com.burkina.marketplace.domain.repository;

import com.burkina.marketplace.domain.entity.SellerPhone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerPhoneRepository extends JpaRepository<SellerPhone, Long> {

    @Modifying
    @Query("""
        DELETE FROM SellerPhone sp
        WHERE sp.id = :id
        AND sp.seller.userId = :userId
    """)
    void deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
