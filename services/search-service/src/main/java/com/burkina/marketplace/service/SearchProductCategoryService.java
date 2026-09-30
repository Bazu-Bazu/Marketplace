package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.SearchCategory;
import com.burkina.marketplace.domain.entity.SearchProduct;
import com.burkina.marketplace.domain.entity.SearchProductCategory;
import com.burkina.marketplace.domain.repository.SearchProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchProductCategoryService {

    private final SearchCategoryService categoryService;
    private final SearchProductCategoryRepository productCategoryRepository;

    @Transactional
    public void addProductCategories(SearchProduct product, List<Long> categoryIds) {
        Map<Long, SearchCategory> categories = categoryService.getCategoriesById(categoryIds);

        List<SearchProductCategory> productCategories = categoryIds.stream()
                .map(pc -> {
                    SearchCategory category = categories.get(pc);

                    return SearchProductCategory.builder()
                            .category(category)
                            .build();
                })
                .toList();

        productCategories.forEach(product::addCategory);
    }

    @Transactional
    public void syncProductCategories(SearchProduct product, List<Long> categoryIds) {
        List<SearchProductCategory> productCategories = getProductCategoriesByProduct(product);

        List<SearchProductCategory> removedCategories = productCategories.stream()
                    .filter(pc -> !categoryIds.contains(pc.getCategory().getId()))
                    .toList();

        removedCategories.forEach(product::removeCategory);

        List<Long> newCategoryIds = categoryIds.stream()
                    .filter(pc -> productCategories.stream()
                            .noneMatch(p -> p.getCategory().getId().equals(pc)))
                    .toList();

        if (!newCategoryIds.isEmpty()) {
            addProductCategories(product, newCategoryIds);
        };
    }

    @Transactional(readOnly = true)
    public List<SearchProductCategory> getProductCategoriesByProduct(SearchProduct product) {
        return productCategoryRepository.findAllByProduct(product);
    }
}
