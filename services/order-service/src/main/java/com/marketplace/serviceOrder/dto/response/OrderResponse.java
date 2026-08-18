package com.marketplace.serviceOrder.dto.response;

import com.marketplace.serviceOrder.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderResponse {

    private Long id;
    private Long userId;
    private OrderStatus status;
    private Integer totalPrice;
    private List<OrderItemResponse> items;
    private String paymentId;
    private String address;

}
