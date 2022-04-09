package dev.asiglesias.infrastructure.repository.mongo;

import dev.asiglesias.infrastructure.entity.mongo.ProductEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductMongoRepository extends MongoRepository<ProductEntity, String> {
}
