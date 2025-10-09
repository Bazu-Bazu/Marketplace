package com.marketplace.serviceOrder.service;

import com.marketplace.serviceOrder.dto.request.AddItemRequest;
import com.marketplace.serviceOrder.dto.response.BasketItemResponse;
import com.marketplace.serviceOrder.entity.Basket;
import com.marketplace.serviceOrder.entity.BasketItem;
import com.marketplace.serviceOrder.repository.BasketItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasketItemService {

    private final BasketItemRepository basketItemRepository;
    private final BasketService basketService;

    public BasketItemResponse addItem(Long userId, AddItemRequest request) {
        Basket basket = basketService.findBasketByUserId(userId);

        Optional<BasketItem> basketItem = basketItemRepository.findByBasketAndProductId(basket, request.getProductId());

        BasketItem item;

        if (basketItem.isPresent()) {
            item = basketItem.get();
            int currentCount = item.getCount();
            int newCount = currentCount + request.getCount();
            item.setCount(newCount);
        }
        else {
            item = new BasketItem();
            item.setProductId(request.getProductId());
            item.setCount(request.getCount());
            item.setBasket(basket);
        }

        basketItemRepository.save(item);

        return buildBasketItemResponse(item);
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
