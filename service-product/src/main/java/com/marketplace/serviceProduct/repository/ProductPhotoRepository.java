package com.marketplace.serviceProduct.repository;

import com.marketplace.serviceProduct.entity.ProductPhoto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPhotoRepository extends MongoRepository<ProductPhoto, Long> {
}
