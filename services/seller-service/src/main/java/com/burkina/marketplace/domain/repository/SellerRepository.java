package com.burkina.marketplace.domain.repository;

import com.burkina.marketplace.domain.entity.Seller;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    boolean existsByUserId(Long userId);
    boolean existsByInn(String inn);

    @EntityGraph(attributePaths = {"phones", "emails", "bankAccounts"})
    Optional<Seller> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"phones"})
    Optional<Seller> findWithPhonesByUserId(Long userId);

    @EntityGraph(attributePaths = {"emails"})
    Optional<Seller> findWithEmailsByUserId(Long userId);

    @EntityGraph(attributePaths = {"bankAccounts"})
    Optional<Seller> findWithBankAccountsByUserId(Long userId);
}
