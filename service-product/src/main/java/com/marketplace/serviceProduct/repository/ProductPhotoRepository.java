package com.marketplace.serviceProduct.repository;

import com.marketplace.serviceProduct.entity.ProductPhoto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductPhotoRepository extends MongoRepository<ProductPhoto, Long> {

    Optional<ProductPhoto> findById(String id);
    List<ProductPhoto> findByProductIdOrderBySortOrderAsc(Long productId);
    void deleteByProductId(Long productId);
    void deleteById(String id);

}
