package dev.asiglesias.domain.repository;

import dev.asiglesias.domain.Meal;
import dev.asiglesias.domain.User;

import java.util.List;

public interface MealRepository {
    List<Meal> getMealsForUser(User user);
}
