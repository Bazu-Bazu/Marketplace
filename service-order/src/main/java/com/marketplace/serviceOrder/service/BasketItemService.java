package com.marketplace.serviceOrder.service;

import com.marketplace.serviceOrder.dto.request.AddItemRequest;
import com.marketplace.serviceOrder.dto.response.BasketItemResponse;
import com.marketplace.serviceOrder.entity.Basket;
import com.marketplace.serviceOrder.entity.BasketItem;
import com.marketplace.serviceOrder.exception.ProductException;
import com.marketplace.serviceOrder.repository.BasketItemRepository;
import com.marketplace.serviceOrder.service.grpc.ProductGrpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasketItemService {

    private final BasketItemRepository basketItemRepository;
    private final BasketService basketService;
    private final ProductGrpcClient productGrpcClient;

    public BasketItemResponse addItem(Long userId, AddItemRequest request) {
        boolean productExist = productGrpcClient.validateProduct(request.getProductId());
        if (!productExist) {
            throw new ProductException("Product not found.");
        }

        Basket basket = basketService.findBasketByUserId(userId);

        BasketItem basketItem = basketItemRepository.findByBasketAndProductId(basket, request.getProductId())
                .orElseGet(() -> createBasketItem(basket, request.getProductId()));

        int newCount = basketItem.getCount() + request.getCount();
        basketItem.setCount(newCount);

        basketItemRepository.save(basketItem);

        return buildBasketItemResponse(basketItem);
    }

    private BasketItem createBasketItem(Basket basket, Long productId) {
        BasketItem item = new BasketItem();
        item.setProductId(productId);
        item.setBasket(basket);

        return item;
    }

    private BasketItemResponse buildBasketItemResponse(BasketItem item) {
        return BasketItemResponse.builder()
                .itemId(item.getId())
                .basketId(item.getBasket().getId())
                .productId(item.getProductId())
                .count(item.getCount())
                .build();
    }

}
