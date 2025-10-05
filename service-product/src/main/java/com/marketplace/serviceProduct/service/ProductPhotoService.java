package com.marketplace.serviceProduct.service;

import com.marketplace.serviceProduct.dto.response.ProductPhotoResponse;
import com.marketplace.serviceProduct.entity.ProductPhoto;
import com.marketplace.serviceProduct.exception.ProductPhotoException;
import com.marketplace.serviceProduct.repository.ProductPhotoRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductPhotoService {

    private final ProductPhotoRepository productPhotoRepository;
    private final ProductService productService;
    private final GridFsTemplate gridFsTemplate;

    @Transactional
    public List<ProductPhotoResponse> addProductPhotos(List<MultipartFile> photos, Long productId) {
        productService.findProductById(productId);

        List<ProductPhoto> newPhotos = photos.stream()
                .map(photo -> createNewProductPhoto(photo, productId))
                .toList();

        List<ProductPhoto> savedPhotos = productPhotoRepository.saveAll(newPhotos);

        return savedPhotos.stream()
                .map(this::buildProductPhotoResponse)
                .toList();
    }

    private ProductPhoto createNewProductPhoto(MultipartFile photo, Long productId) {
        try {
            ObjectId fileId = gridFsTemplate.store(
                    photo.getInputStream(),
                    photo.getOriginalFilename(),
                    photo.getContentType()
            );

            ProductPhoto newPhoto = new ProductPhoto();
            newPhoto.setProductId(productId);
            newPhoto.setFileName(photo.getOriginalFilename());
            newPhoto.setContentType(photo.getContentType());
            newPhoto.setSize(photo.getSize());
            newPhoto.setUrl("/product-photo/" + fileId);

            return newPhoto;
        } catch (IOException e) {
            throw new ProductPhotoException("Creating ProductPhoto error.");
        }
    }

    private ProductPhotoResponse buildProductPhotoResponse(ProductPhoto photo) {
        return ProductPhotoResponse.builder()
                .id(photo.getId())
                .name(photo.getFileName())
                .contentType(photo.getContentType())
                .size(photo.getSize())
                .productId(photo.getProductId())
                .url(photo.getUrl())
                .build();
    }

}
