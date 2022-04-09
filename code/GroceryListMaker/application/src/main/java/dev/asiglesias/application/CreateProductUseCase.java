package dev.asiglesias.application;
import dev.asiglesias.domain.MeasureUnit;
import dev.asiglesias.domain.Product;
import lombok.Data;

import java.util.List;

public interface CreateProductUseCase {
    Product create(String name, List<String> alternativeNames, MeasureUnit unit);
}