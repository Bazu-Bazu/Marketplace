package com.marketplace.serviceProduct.service;

import lombok.RequiredArgsConstructor;
import com.marketplace.serviceProduct.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

}
