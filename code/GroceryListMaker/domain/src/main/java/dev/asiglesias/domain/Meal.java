package dev.asiglesias.domain;

import lombok.Value;

import java.util.List;

@Value
public class Meal {
    List<Ingredient> ingredients;
    int servings;
}
