package com.marketplace.serviceOrder.repository;

import com.marketplace.serviceOrder.entity.BasketItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketItemRepository extends JpaRepository<BasketItem, Long> {
}
