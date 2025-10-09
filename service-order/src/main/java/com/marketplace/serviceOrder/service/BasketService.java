package com.marketplace.serviceOrder.service;

import com.marketplace.serviceOrder.entity.Basket;
import com.marketplace.serviceOrder.repository.BasketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasketService {

    private final BasketRepository basketRepository;

    @Transactional
    public void createBasket(Long userId) {
        Basket basket = new Basket();
        basket.setUserId(userId);

        basketRepository.save(basket);
    }

}
