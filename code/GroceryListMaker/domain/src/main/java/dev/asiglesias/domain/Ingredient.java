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
}
