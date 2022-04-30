package dev.asiglesias.domain.repository;

import dev.asiglesias.domain.Meal;

import java.util.List;

public interface MealRepository {
    List<Meal> getMeals();
}
