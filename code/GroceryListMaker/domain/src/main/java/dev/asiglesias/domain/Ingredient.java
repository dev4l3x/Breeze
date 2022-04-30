package dev.asiglesias.domain;

import lombok.Builder;
import lombok.Value;

import java.util.OptionalDouble;

@Value
@Builder
public class Ingredient {
    double quantity;
    MeasureUnit unit;
    Product product;

    @Override
    public String toString() {
        if(quantity == 0) {
            return String.format("%s %s", unit, product);
        }
        return String.format("%s%s %s", quantity, unit, product);
    }

}
