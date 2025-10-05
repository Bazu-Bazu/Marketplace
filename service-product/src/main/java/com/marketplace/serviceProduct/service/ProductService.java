package com.marketplace.serviceProduct.service;

import com.marketplace.serviceProduct.entity.Category;
import com.marketplace.serviceProduct.entity.Product;
import com.marketplace.serviceProduct.exception.CategoryException;
import com.marketplace.serviceProduct.exception.ProductException;
import com.marketplace.serviceProduct.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import com.marketplace.serviceProduct.dto.request.AddProductRequest;
import com.marketplace.serviceProduct.dto.response.ProductResponse;
import com.marketplace.serviceProduct.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public List<ProductResponse> addProducts(Long sellerId, List<AddProductRequest> requests) {
        Set<Long> allCategoryIds = requests.stream()
                .flatMap(request -> request.getCategoryIds().stream())
                .collect(Collectors.toSet());

        Map<Long, Category> categoryMap = categoryRepository.findAllByIdIn(allCategoryIds).stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));

        validateCategoriesExist(allCategoryIds, categoryMap.keySet());

        List<Product> newProducts = requests.stream()
                .map(request -> createNewProduct(sellerId, request, categoryMap))
                .toList();

        List<Product> savedProducts = productRepository.saveAll(newProducts);

        return savedProducts.stream()
                .map(this::buildProductResponse)
                .toList();
    }

    private void validateCategoriesExist(Set<Long> requestedIds, Set<Long> existingIds) {
        Set<Long> missingIds = requestedIds.stream()
                .filter(id -> !existingIds.contains(id))
                .collect(Collectors.toSet());

        if (!missingIds.isEmpty()) {
            throw new CategoryException("Categories not found: " + missingIds);
        }
    }

    private Product createNewProduct(Long sellerId, AddProductRequest request, Map<Long, Category> categoryMap) {
        Set<Category> categories = request.getCategoryIds().stream()
                .map(categoryMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setCount(request.getCount());
        product.setDescription(request.getDescription());
        product.setCategories(categories);
        product.setSellerId(sellerId);

        return product;
    }

    public Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductException("Product not found."));
    }

    private ProductResponse buildProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .count(product.getCount())
                .sellerId(product.getSellerId())
                .categoryIds(product.getCategories().stream()
                        .map(Category::getId)
                        .collect(Collectors.toSet()))
                .photoUrls(product.getUrls())
                .build();
    }

}
