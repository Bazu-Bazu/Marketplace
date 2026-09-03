package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.Order;
import com.burkina.marketplace.domain.entity.OrderItem;
import com.burkina.marketplace.dto.data.OrderItemData;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    public List<OrderItem> createOrderItems(List<OrderItemData> itemsData, Order order) {
        return itemsData.stream()
                .map(itemData -> createOrderItem(itemData, order))
                .toList();
    }

    private OrderItem createOrderItem(OrderItemData itemData, Order order) {
        return OrderItem.builder()
                .productId(itemData.productId())
                .price(itemData.price())
                .order(order)
                .build();
    }
}
