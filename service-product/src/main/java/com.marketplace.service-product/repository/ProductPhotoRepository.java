package marketplace.serviceproduct.repository;

import marketplace.serviceproduct.entity.ProductPhoto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPhotoRepository extends MongoRepository<ProductPhoto, Long> {
}
