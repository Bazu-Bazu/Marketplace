package com.marketplace.serviceOrder.repository;

import com.marketplace.serviceOrder.entity.Basket;
import com.marketplace.serviceOrder.entity.BasketItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BasketItemRepository extends JpaRepository<BasketItem, Long> {

    Optional<BasketItem> findByBasketAndProductId(Basket basket, Long productId);

}
