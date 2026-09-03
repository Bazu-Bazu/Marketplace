package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.Order;
import com.burkina.marketplace.domain.entity.OrderItem;
import com.burkina.marketplace.domain.repository.OrderRepository;
import com.burkina.marketplace.dto.data.CreateOrderData;
import com.burkina.marketplace.exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;

    @Transactional
    public Order createOrder(CreateOrderData data) {
        Order order = Order.builder()
                .userId(data.userId())
                .totalPrice(data.totalPrice())
                .paymentId(data.paymentId())
                .build();

        List<OrderItem> items = orderItemService.createOrderItems(data.items(), order);

        order.addItems(items);

        return orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = getById(orderId);

        order.cancel();
    }

    @Transactional(readOnly = true)
    public Order getById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        String.format("Order %d not found", orderId)
                ));
    }
}
