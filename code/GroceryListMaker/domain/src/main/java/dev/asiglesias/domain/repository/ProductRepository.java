package dev.asiglesias.domain.repository;

import dev.asiglesias.domain.Product;

public interface ProductRepository {
    Product save(Product product);
}
