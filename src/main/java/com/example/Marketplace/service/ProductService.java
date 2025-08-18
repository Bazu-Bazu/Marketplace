package com.example.Marketplace.service;

import com.example.Marketplace.dto.AddProductDto;
import com.example.Marketplace.dto.response.ProductResponse;
import com.example.Marketplace.model.Product;
import com.example.Marketplace.model.Seller;
import com.example.Marketplace.repository.ProductRepository;
import com.example.Marketplace.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;

    @Transactional
    public ProductResponse addProduct(String email, AddProductDto addProductDto) {
        Optional<Seller> seller = sellerRepository.findByEmail(email);

        if (seller.isEmpty()) {
            return ProductResponse.builder()
                    .message("You are not a seller.")
                    .build();
        }

        Product product = new Product();
        product.setName(addProductDto.getName());
        product.setDescription(addProductDto.getDescription());
        product.setPrice(addProductDto.getPrice());
        product.setCount(addProductDto.getCount());
        product.setSeller(seller.get());
        productRepository.save(product);

        return ProductResponse.builder()
                .message("Product added successfully.")
                .build();
    }

}
