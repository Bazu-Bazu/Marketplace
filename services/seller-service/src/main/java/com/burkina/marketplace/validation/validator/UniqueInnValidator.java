package com.burkina.marketplace.validation.validator;

import com.burkina.marketplace.domain.repository.SellerRepository;
import com.burkina.marketplace.validation.annotation.UniqueInn;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueInnValidator implements ConstraintValidator<UniqueInn, String> {

    private final SellerRepository sellerRepository;

    @Override
    public boolean isValid(String inn, ConstraintValidatorContext context) {
        return !sellerRepository.existsByInn(inn);
    }
}
