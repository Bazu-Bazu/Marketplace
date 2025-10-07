package com.marketplace.serviceProduct.service;

import com.marketplace.serviceProduct.dto.response.ProductPhotoResponse;
import com.marketplace.serviceProduct.entity.ProductPhoto;
import com.marketplace.serviceProduct.exception.ProductPhotoException;
import com.marketplace.serviceProduct.repository.ProductPhotoRepository;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

        List<String> photoUrls = savedPhotos.stream()
                .map(ProductPhoto::getUrl)
                .toList();

        productService.addPhotos(productId, photoUrls);

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

    public ResponseEntity<Resource> downloadPhoto(String fileId) {
        GridFSFile gridFSFile = gridFsTemplate.findOne(
            new Query(Criteria.where("_id").is(new ObjectId(fileId))));

        GridFsResource resource =gridFsTemplate.getResource(gridFSFile);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resource.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
