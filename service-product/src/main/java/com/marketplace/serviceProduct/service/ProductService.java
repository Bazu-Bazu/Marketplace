package com.marketplace.serviceProduct.service;

import com.marketplace.serviceProduct.dto.response.ProductDetailsResponse;
import com.marketplace.serviceProduct.dto.response.ProductShortResponse;
import com.marketplace.serviceProduct.entity.Category;
import com.marketplace.serviceProduct.entity.Product;
import com.marketplace.serviceProduct.exception.CategoryException;
import com.marketplace.serviceProduct.exception.ProductException;
import com.marketplace.serviceProduct.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import com.marketplace.serviceProduct.dto.request.AddProductRequest;
import com.marketplace.serviceProduct.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public List<ProductDetailsResponse> addProducts(Long sellerId, String sellerName, List<AddProductRequest> requests) {
        Set<Long> allCategoryIds = requests.stream()
                .flatMap(request -> request.getCategoryIds().stream())
                .collect(Collectors.toSet());

        Map<Long, Category> categoryMap = categoryRepository.findAllByIdIn(allCategoryIds).stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));

        validateCategoriesExist(allCategoryIds, categoryMap.keySet());

        List<Product> newProducts = requests.stream()
                .map(request -> createNewProduct(sellerId, sellerName, request, categoryMap))
                .toList();

        List<Product> savedProducts = productRepository.saveAll(newProducts);

        return savedProducts.stream()
                .map(this::buildProductDetailsResponse)
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

    private Product createNewProduct(
            Long sellerId, String sellerName,
            AddProductRequest request, Map<Long, Category> categoryMap)
    {
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
        product.setSellerName(sellerName);

        return product;
    }

    public Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductException("Product not found."));
    }

    public Page<ProductShortResponse> getProductShort(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);

        return products.map(this::buildProductShortResponse);
    }

    private ProductShortResponse buildProductShortResponse(Product product) {
        return ProductShortResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .rating(product.getRating())
                .price(product.getPrice())
                .photoUrl(!product.getUrls().isEmpty() ? product.getUrls().get(0) : null)
                .sellerId(product.getSellerId())
                .build();
    }

    public ProductDetailsResponse getProductDetail(Long productId) {
        Product product = findProductById(productId);

        return buildProductDetailsResponse(product);
    }

    private ProductDetailsResponse buildProductDetailsResponse(Product product) {
        return  ProductDetailsResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .count(product.getCount())
                .categoryIds(product.getCategories().stream()
                        .map(Category::getId)
                        .collect(Collectors.toSet()))
                .categoryNames(product.getCategories().stream()
                        .map(Category::getName)
                        .collect(Collectors.toSet()))
                .sellerId(product.getSellerId())
                .sellerName(product.getSellerName())
                .photoUrls(product.getUrls())
                .build();
    }

    public void validateProductOwnership(Long sellerId, Long productId) throws AccessDeniedException {
        Product product = findProductById(productId);

        if (!sellerId.equals(product.getSellerId())) {
            throw new AccessDeniedException("Product doesn't belong to current seller.");
        }
    }

    @Transactional
    public void addPhotos(Long productId, List<String> photoUrls) {
        Product product = findProductById(productId);
        product.getUrls().addAll(photoUrls);

        productRepository.save(product);
    }

    @Transactional
    public void changeProductRating(Long productId, Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new ProductException("Illegal rating.");
        }

        Product product = findProductById(productId);

        int newRatingCount = (product.getCount() != null ? product.getCount() : 0) + 1;
        int newTotalRating = (product.getTotalRating() != null ? product.getTotalRating() : 0) + rating;
        double newRating = (double) newTotalRating / newRatingCount;

        product.setRatingCount(newRatingCount);
        product.setTotalRating(newTotalRating);
        product.setRating(newRating);

        productRepository.save(product);
    }

}
