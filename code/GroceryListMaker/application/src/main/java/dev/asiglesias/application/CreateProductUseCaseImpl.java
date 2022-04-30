package dev.asiglesias.application;

import dev.asiglesias.domain.MeasureUnit;
import dev.asiglesias.domain.Product;
import dev.asiglesias.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CreateProductUseCaseImpl implements CreateProductUseCase {

    private final ProductRepository repository;

    @Override
    public Product create(String name, List<String> alternativeNames, MeasureUnit unit) {
        Product newProduct = new Product(name);
        return repository.save(newProduct);
    }
}
