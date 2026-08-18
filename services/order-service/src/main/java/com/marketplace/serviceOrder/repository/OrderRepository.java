package com.marketplace.serviceOrder.repository;

import com.marketplace.serviceOrder.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {



}
