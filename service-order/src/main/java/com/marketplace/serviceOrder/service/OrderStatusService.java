package com.marketplace.serviceOrder.service;

import com.marketplace.serviceOrder.entity.Order;
import com.marketplace.serviceOrder.enums.OrderStatus;
import com.marketplace.serviceOrder.exception.OrderStatusException;
import com.marketplace.serviceOrder.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderStatusService {

    private final OrderRepository orderRepository;

    private final Map<OrderStatus, Set<OrderStatus>> allowedTransitions = Map.of(
            OrderStatus.PENDING, Set.of(OrderStatus.PAID, OrderStatus.CANCELLED),
            OrderStatus.PAID, Set.of(OrderStatus.PROCESSING, OrderStatus.CANCELLED),
            OrderStatus.PROCESSING, Set.of(OrderStatus.SHIPPED),
            OrderStatus.SHIPPED, Set.of(OrderStatus.DELIVERED),
            OrderStatus.DELIVERED, Set.of(),
            OrderStatus.CANCELLED, Set.of()
    );

    @Transactional
    public Order transitionTo(Order order, OrderStatus newStatus) {
        OrderStatus currentStatus = order.getStatus();

        if (currentStatus != null && !isTransitionAllowed(currentStatus, newStatus)) {
            throw new OrderStatusException(
                    String.format("Cannot transition from %s to %s", currentStatus, newStatus)
            );
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    private boolean isTransitionAllowed(OrderStatus from, OrderStatus to) {
        return allowedTransitions.get(from).contains(to);
    }

}
