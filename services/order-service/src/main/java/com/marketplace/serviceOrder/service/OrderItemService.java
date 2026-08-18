package com.marketplace.serviceOrder.service;

import com.marketplace.serviceOrder.dto.event.ProductEvent;
import com.marketplace.serviceOrder.dto.request.SetOrderItemRatingRequest;
import com.marketplace.serviceOrder.dto.response.OrderItemResponse;
import com.marketplace.serviceOrder.entity.Basket;
import com.marketplace.serviceOrder.entity.BasketItem;
import com.marketplace.serviceOrder.entity.Order;
import com.marketplace.serviceOrder.entity.OrderItem;
import com.marketplace.serviceOrder.exception.OrderItemException;
import com.marketplace.serviceOrder.repository.OrderItemRepository;
import com.marketplace.serviceOrder.service.handler.ProductEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final ProductEventPublisher productEventPublisher;

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
                .rating(orderItem.getRating())
                .build();
    }

    public OrderItem findOrderItemById(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new OrderItemException("Order item not found."));
    }

    @Transactional
    public OrderItemResponse setOrderItemRating(SetOrderItemRatingRequest request) {
        OrderItem orderItem = findOrderItemById(request.getOrderItemId());

        if (orderItem.getRating() != null) {
            throw new OrderItemException("Order item already have rating.");
        }

        int rating = request.getRating();
        if (rating < 0 || rating > 5) {
            throw new OrderItemException("Illegal order item rating.");
        }

        ProductEvent event = ProductEvent.builder()
                .id(orderItem.getProductId())
                .rating(rating)
                .build();

        orderItem.setRating(rating);
        orderItemRepository.save(orderItem);

        productEventPublisher.sendProductToKafka(event);

        return createOrderItemResponse(orderItem);
    }

}
