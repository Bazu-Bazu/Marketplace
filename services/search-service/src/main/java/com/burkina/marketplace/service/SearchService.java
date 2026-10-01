package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.SearchProduct;
import com.burkina.marketplace.domain.repository.SearchProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchProductRepository productRepository;

    public Page<SearchProduct> search(String query, Pageable pageable) {
        return productRepository.search(query, pageable);
    }
}
