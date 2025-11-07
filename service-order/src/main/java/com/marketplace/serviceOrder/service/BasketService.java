package com.marketplace.serviceOrder.service;

import com.marketplace.serviceOrder.dto.grpc.BasketValidationResult;
import com.marketplace.serviceOrder.entity.Basket;
import com.marketplace.serviceOrder.entity.BasketItem;
import com.marketplace.serviceOrder.exception.BasketException;
import com.marketplace.serviceOrder.repository.BasketRepository;
import com.marketplace.serviceOrder.service.grpc.ProductGrpcClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class BasketService {

    private final BasketItemService basketItemService;
    private final BasketRepository basketRepository;
    private final ProductGrpcClient productGrpcClient;

    public BasketService(@Lazy BasketItemService basketItemService, BasketRepository basketRepository,
                         ProductGrpcClient productGrpcClient) {
        this.basketItemService = basketItemService;
        this.basketRepository = basketRepository;
        this.productGrpcClient = productGrpcClient;
    }

    @Transactional
    public void createBasket(Long userId) {
        Basket basket = new Basket();
        basket.setUserId(userId);

        basketRepository.save(basket);
    }

    public Basket findBasketByUserId(Long userId) {
        return basketRepository.findByUserId(userId)
                .orElseThrow(() -> new BasketException("Basket not found."));
    }

    public void updateBasketTotalPrice(Basket basket) {
        List<BasketItem> items = basket.getItems();

        int totalPrice = items.stream()
                .mapToInt(item -> {
                    int price = item.getPrice() != null ? item.getPrice() : 0;
                    int count = item.getCount() != null ? item.getCount() : 0;
                    return price * count;
                })
                .sum();

        basket.setTotalPrice(totalPrice);
        basketRepository.save(basket);
    }

    public BasketValidationResult validateBasket(Basket basket) {
        List<BasketItem> items = basket.getItems();

        if (items.isEmpty()) {
            return new BasketValidationResult(Map.of());
        }

        BasketValidationResult validationResult = productGrpcClient.validateBasketItems(items);

        if (validationResult.hasNotExistItems()) {
            basketItemService.deleteFromBasketNotExistItems(items, validationResult);
        }

        if (validationResult.hasNotCountSufficientItems()) {
            basketItemService.changeItemsCount(items, validationResult);
        }

        basketItemService.updateBasketItemPrices(items, validationResult);
        updateBasketTotalPrice(basket);

        return validationResult;
    }

}
