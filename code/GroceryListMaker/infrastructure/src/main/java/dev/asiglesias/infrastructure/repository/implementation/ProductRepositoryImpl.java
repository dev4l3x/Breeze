package dev.asiglesias.infrastructure.repository.implementation;

import dev.asiglesias.domain.Product;
import dev.asiglesias.domain.ProductRepository;
import dev.asiglesias.infrastructure.entity.mongo.ProductEntity;
import dev.asiglesias.infrastructure.mappers.entity.ProductEntityMapper;
import dev.asiglesias.infrastructure.repository.mongo.ProductMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ProductRepositoryImpl implements ProductRepository {

    final private ProductMongoRepository repository;

    final private ProductEntityMapper mapper;

    @Override
    public Product save(Product product) {
       ProductEntity entity = repository.save(mapper.toEntity(product));
       return mapper.toDomain(entity);
    }
}
