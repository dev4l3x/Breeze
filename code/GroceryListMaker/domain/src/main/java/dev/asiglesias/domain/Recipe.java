package dev.asiglesias.domain;

import lombok.Value;

import java.util.List;

@Value
public class Recipe {
    List<Ingredient> ingredients;
    int quantity;
}
