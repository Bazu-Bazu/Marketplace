package com.burkina.marketplace.validation.validator;

import com.burkina.marketplace.domain.repository.CategoryRepository;
import com.burkina.marketplace.validation.annotation.UniqueCategory;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueCategoryValidator implements ConstraintValidator<UniqueCategory, String> {

    private final CategoryRepository categoryRepository;

    @Override
    public boolean isValid(String categoryName, ConstraintValidatorContext context) {
        if (categoryName == null || categoryName.isBlank()) {
            return false;
        }

        return !categoryRepository.existsByNameIgnoreCase(categoryName);
    }
}
