package dev.asiglesias.domain;

import dev.asiglesias.domain.exception.ProductException;
import dev.asiglesias.domain.exception.code.ProductExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {

    @EqualsAndHashCode.Include private String name;

    private List<String> alternativeNames;

    private MeasureUnit preferredUnit;

    public Product(String name, List<String> alternativeNames, MeasureUnit preferredUnit) {
        setName(name);
        setAlternativeNames(alternativeNames);
        setPreferredUnit(preferredUnit);
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

    public List<String> getAlternativeNames() {
        return List.copyOf(alternativeNames);
    }

    public void setAlternativeNames(List<String> alternativeNames) {
        if (alternativeNames == null) {
            this.alternativeNames = new ArrayList<>();
            return;
        }
        this.alternativeNames = alternativeNames;
    }

    public MeasureUnit getPreferredUnit() {
        return preferredUnit;
    }

    public void setPreferredUnit(MeasureUnit preferredUnit) {
        this.preferredUnit = preferredUnit;
    }
}
