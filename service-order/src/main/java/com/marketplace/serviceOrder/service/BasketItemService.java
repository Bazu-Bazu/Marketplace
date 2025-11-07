package com.marketplace.serviceOrder.service;

import com.marketplace.serviceOrder.dto.grpc.BasketItemValidationResult;
import com.marketplace.serviceOrder.dto.grpc.BasketValidationResult;
import com.marketplace.serviceOrder.dto.request.AddItemRequest;
import com.marketplace.serviceOrder.dto.response.BasketItemResponse;
import com.marketplace.serviceOrder.entity.Basket;
import com.marketplace.serviceOrder.entity.BasketItem;
import com.marketplace.serviceOrder.exception.ProductException;
import com.marketplace.serviceOrder.repository.BasketItemRepository;
import com.marketplace.serviceOrder.service.grpc.ProductGrpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
                .orElseGet(() -> createBasketItem(basket, request));

        int newCount = basketItem.getCount() + request.getCount();
        basketItem.setCount(newCount);

        basketItemRepository.save(basketItem);

        basketService.updateBasketTotalPrice(basket);

        return buildBasketItemResponse(basketItem);
    }

    public void updateBasketItemPrices(List<BasketItem> items, BasketValidationResult validationResult) {
        List<BasketItem> updatedItems = items.stream().map(item -> {
            Long productId = item.getProductId();

            BasketItemValidationResult itemValidationResult = validationResult.getResultForItem(productId);

            if (!item.getPrice().equals(itemValidationResult.getCurrentPrice())) {
                item.setPrice(itemValidationResult.getCurrentPrice());
            }

            return item;
        })
        .toList();

        basketItemRepository.saveAll(updatedItems);
    }

    public void deleteFromBasketNotExistItems(List<BasketItem> items, BasketValidationResult validationResult) {
        for (BasketItem item : items) {
            BasketItemValidationResult result = validationResult.getResultForItem(item.getProductId());

            if (result == null) {
                basketItemRepository.delete(item);
                items.remove(item);
            }
        }
    }

    public void changeItemsCount(List<BasketItem> items, BasketValidationResult validationResult) {
        items.forEach(item -> {
            BasketItemValidationResult result = validationResult.getResultForItem(item.getProductId());
            if (!result.isCountSufficient()) {
                item.setCount(result.getAvailableCount());
                basketItemRepository.save(item);
            }
        });
    }

    private BasketItem createBasketItem(Basket basket, AddItemRequest request) {
        BasketItem item = new BasketItem();
        item.setProductId(request.getProductId());
        item.setCount(request.getCount());
        item.setPrice(request.getPrice());
        item.setBasket(basket);

        basket.getItems().add(item);

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
