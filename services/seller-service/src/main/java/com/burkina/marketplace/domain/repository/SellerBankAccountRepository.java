package com.burkina.marketplace.domain.repository;

import com.burkina.marketplace.domain.entity.SellerBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerBankAccountRepository extends JpaRepository<SellerBankAccount, Long> {

    @Modifying
    @Query("""
        DELETE FROM SellerBankAccount sb
        WHERE sb.id = :id
        AND sb.seller.userId = :userId
    """)
    void deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
