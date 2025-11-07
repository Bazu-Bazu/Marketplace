package com.marketplace.serviceOrder.service;

import com.marketplace.serviceOrder.dto.response.OrderItemResponse;
import com.marketplace.serviceOrder.entity.Basket;
import com.marketplace.serviceOrder.entity.BasketItem;
import com.marketplace.serviceOrder.entity.Order;
import com.marketplace.serviceOrder.entity.OrderItem;
import com.marketplace.serviceOrder.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public List<OrderItem> createOrderItemsFromBasket(Basket basket, Order order) {
        return basket.getItems().stream()
                .map(basketItem -> createOrderItem(basketItem, order))
                .collect(Collectors.toList());
    }

    private OrderItem createOrderItem(BasketItem basketItem, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProductId(basketItem.getProductId());
        orderItem.setPrice(basketItem.getPrice());
        orderItem.setCount(basketItem.getCount());

        return orderItemRepository.save(orderItem);
    }

    public OrderItemResponse createOrderItemResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProductId())
                .price(orderItem.getPrice())
                .count(orderItem.getCount())
                .build();
    }

}
