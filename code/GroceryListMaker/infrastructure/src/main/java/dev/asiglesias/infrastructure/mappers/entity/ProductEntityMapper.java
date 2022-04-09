package dev.asiglesias.infrastructure.mappers.entity;

import dev.asiglesias.domain.Product;
import dev.asiglesias.infrastructure.entity.mongo.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductEntityMapper {

    Product toDomain(ProductEntity entity);

    ProductEntity toEntity(Product product);

}
