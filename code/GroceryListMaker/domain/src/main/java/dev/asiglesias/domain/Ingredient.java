package dev.asiglesias.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Ingredient {
    double quantity;
    MeasureUnit unit;
    Product product;

    public Ingredient multiplyQuantityBy(int multiplier) {
        return new Ingredient(quantity * multiplier, unit, product);
    }

    @Override
    public String toString() {
        if(quantity == 0) {
            return String.format("%s %s", unit, product);
        }
        return String.format("%s%s %s", quantity, unit, product);
    }

}
