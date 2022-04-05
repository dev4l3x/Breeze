package dev.asiglesias.domain;

import lombok.Value;

@Value
public class Ingredient {
    float quantity;
    MeasureUnit unit;
    Product product;
}
