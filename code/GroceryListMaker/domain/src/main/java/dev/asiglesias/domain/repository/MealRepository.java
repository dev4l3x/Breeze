package dev.asiglesias.domain.repository;

import dev.asiglesias.domain.Recipe;
import dev.asiglesias.domain.User;

import java.util.List;

public interface MealRepository {
    List<Recipe> getMealsForUser(User user);
}
