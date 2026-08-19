package com.burkina.marketplace.validation.validator;

import com.burkina.marketplace.domain.repository.SellerRepository;
import com.burkina.marketplace.exception.UserAlreadySellerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SellerValidator {

    private final SellerRepository sellerRepository;

    public void validateUserIsNotSeller(Long userId) {
        if (sellerRepository.existsByUserId(userId)) {
            throw new UserAlreadySellerException(
                    String.format("User with id %d is already a seller", userId)
            );
        }
    }
}
