package com.marketplace.serviceProduct.service;

import com.marketplace.serviceProduct.entity.Category;
import com.marketplace.serviceProduct.entity.Product;
import com.marketplace.serviceProduct.entity.ProductPhoto;
import com.marketplace.serviceProduct.exception.InvalidCategoryException;
import com.marketplace.serviceProduct.exception.InvalidSellerIdException;
import com.marketplace.serviceProduct.exception.ProductNotFoundException;
import com.marketplace.serviceProduct.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import com.marketplace.serviceProduct.dto.request.AddProductRequest;
import com.marketplace.serviceProduct.dto.response.ProductResponse;
import com.marketplace.serviceProduct.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductPhotoService productPhotoService;

    @Transactional
    public ProductResponse addProduct(Long sellerId, AddProductRequest request,
                                      List<MultipartFile> imageFiles) throws IOException {
        Set<Category> categories = categoryRepository.findAllByIdIn(request.getCategoryIds());
//        if (categories.size() != request.getCategoryIds().size()) {
//            throw new InvalidCategoryException("Some categories not found.");
//        }

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCount(request.getCount());
        product.setSellerId(sellerId);
        product.setCategories(categories);

        Product saveProduct = productRepository.save(product);

        if (imageFiles != null && !imageFiles.isEmpty()) {
            List<ProductPhoto> photos = productPhotoService.saveProductPhotos(saveProduct.getId(), imageFiles);
            saveProduct.setPhotos(photos);
        }

        return mapToProductResponse(saveProduct);
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found."));

        List<ProductPhoto> photos = productPhotoService.getProductPhotos(id);
        product.setPhotos(photos);

        return mapToProductResponse(product);
    }

    private ProductResponse mapToProductResponse(Product product) {
        List<String> photoUrls = product.getPhotos() != null ?
                product.getPhotos().stream()
                        .map(photo -> "/products/" + product.getId() + "/photos/" + photo.getId())
                        .toList() :
                Collections.emptyList();

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .count(product.getCount())
                .sellerId(product.getSellerId())
                .categories(product.getCategories())
                .photoUrls(photoUrls)
                .build();
    }

    @Transactional
    public void deleteProduct(Long productId, Long sellerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found."));

        if (!product.getId().equals(sellerId)) {
            throw new InvalidSellerIdException("Invalid seller id.");
        }

        productPhotoService.deleteProductPhotos(productId);

        productRepository.delete(product);
    }

}
