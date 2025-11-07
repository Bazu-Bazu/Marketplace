package com.marketplace.serviceOrder.service;

import com.marketplace.serviceOrder.dto.grpc.BasketValidationResult;
import com.marketplace.serviceOrder.dto.response.OrderItemResponse;
import com.marketplace.serviceOrder.dto.response.OrderResponse;
import com.marketplace.serviceOrder.entity.Basket;
import com.marketplace.serviceOrder.entity.Order;
import com.marketplace.serviceOrder.entity.OrderItem;
import com.marketplace.serviceOrder.enums.OrderStatus;
import com.marketplace.serviceOrder.exception.BasketException;
import com.marketplace.serviceOrder.exception.OrderException;
import com.marketplace.serviceOrder.repository.OrderRepository;
import com.marketplace.serviceOrder.service.grpc.PaymentGrpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final BasketService basketService;
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final PaymentGrpcClient paymentGrpcClient;

    @Transactional
    public OrderResponse createOrder(Long userId, String address) {
        Basket basket = basketService.findBasketByUserId(userId);

        if (basket == null || basket.getItems().isEmpty()) {
            throw new BasketException("Cannot create order from empty basket.");
        }

        BasketValidationResult validationResult = basketService.validateBasket(basket);

        Order order = createOrderFromBasket(basket, address);

        try {
            String paymentId = paymentGrpcClient.createPayment(order);
            order.setPaymentId(paymentId);
            orderRepository.save(order);

            return createOrderResponse(order);
        } catch (Exception e) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);

            throw new OrderException("Cannot create payment.");
        }
    }

    private OrderResponse createOrderResponse(Order order) {
        List<OrderItemResponse> orderItemResponses = order.getItems().stream()
                .map(orderItemService::createOrderItemResponse)
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .items(orderItemResponses)
                .paymentId(order.getPaymentId())
                .address(order.getAddress())
                .build();
    }

    @Transactional
    protected Order createOrderFromBasket(Basket basket, String address) {
        Order order = new Order();
        order.setUserId(basket.getUserId());
        order.setStatus(OrderStatus.PENDING);
        order.setAddress(address);
        order.setTotalPrice(basket.getTotalPrice());

        orderRepository.save(order);

        List<OrderItem> orderItems = orderItemService.createOrderItemsFromBasket(basket, order);
        order.setItems(orderItems);

        return order;
    }

}
