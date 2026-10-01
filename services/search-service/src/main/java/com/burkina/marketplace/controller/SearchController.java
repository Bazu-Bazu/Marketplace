package com.burkina.marketplace.controller;

import com.burkina.marketplace.domain.entity.SearchProduct;
import com.burkina.marketplace.dto.response.SearchProductResponse;
import com.burkina.marketplace.mapper.SearchProductMapper;
import com.burkina.marketplace.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private final SearchProductMapper productMapper;

    @GetMapping
    public ResponseEntity<Page<SearchProductResponse>> search(
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<SearchProduct> products = searchService.search(query, pageable);

        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(products.map(productMapper::toResponse));
    }
}
