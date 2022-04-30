package dev.asiglesias.domain.repository;

import dev.asiglesias.domain.Ingredient;

import java.util.List;

public interface GroceryListRepository {
    void create(List<Ingredient> ingredients);
}
