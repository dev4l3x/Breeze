package dev.asiglesias.domain;

import dev.asiglesias.domain.exception.ProductException;
import dev.asiglesias.domain.exception.code.ProductExceptionCode;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Product {

    @EqualsAndHashCode.Include private String name;

    public Product(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (Objects.isNull(name) || name.isBlank()) {
            throw new ProductException(
                    ProductExceptionCode.PRODUCT_MUST_HAVE_NOT_EMPTY_NAME,
                    "Product name cannot have empty value"
            );
        }
        this.name = name;
    }

    public String toString() {
        return this.getName();
    }
}
