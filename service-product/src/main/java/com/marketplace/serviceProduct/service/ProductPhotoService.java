package com.marketplace.serviceProduct.service;

import com.marketplace.serviceProduct.entity.ProductPhoto;
import com.marketplace.serviceProduct.exception.ProductNotFoundException;
import com.marketplace.serviceProduct.repository.ProductPhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductPhotoService {

    private final ProductPhotoRepository productPhotoRepository;

    @Transactional
    public void saveProductPhotos(Long productId, List<MultipartFile> imageFiles) throws IOException {
        List<ProductPhoto> photos = new ArrayList<>();

        for (int i = 0; i < imageFiles.size(); i++) {
            MultipartFile file = imageFiles.get(i);

            ProductPhoto photo = new ProductPhoto();
            photo.setProductId(productId);
            photo.setImageData(file.getBytes());
            photo.setSortOrder(i);
            photo.setIsMain(i == 0);

            photos.add(photo);
        }

        productPhotoRepository.saveAll(photos);
    }

    public List<ProductPhoto> getProductPhotos(Long productId) {
        return productPhotoRepository.findByProductIdOrderBySortOrderAsc(productId);
    }

    public byte[] getProductPhotoImage(String photoId) {
        return productPhotoRepository.findById(photoId)
                .map(ProductPhoto::getImageData)
                .orElseThrow(() -> new ProductNotFoundException("Product not found."));
    }

    public void deleteProductPhotos(Long productId) {
        productPhotoRepository.deleteByProductId(productId);
    }

    public void deleteProductPhoto(String productPhotoId) {
        productPhotoRepository.deleteById(productPhotoId);
    }

}
