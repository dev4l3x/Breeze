package dev.asiglesias.application;

import dev.asiglesias.domain.GroceryList;
import dev.asiglesias.domain.Ingredient;
import dev.asiglesias.domain.Meal;
import dev.asiglesias.domain.User;
import dev.asiglesias.domain.repository.GroceryListRepository;
import dev.asiglesias.domain.repository.MealRepository;
import dev.asiglesias.domain.service.IngredientAggregatorService;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@RequiredArgsConstructor
public class GenerateGroceryListUseCaseImpl implements GenerateGroceryListUseCase {

    private final IngredientAggregatorService aggregatorService;

    private final MealRepository mealRepository;

    private final GroceryListRepository groceryListRepository;

    @Override
    public void generateForUser(User user) {
        List<Meal> meals = mealRepository.getMealsForUser(user);

        List<Ingredient> neededIngredientsForMeals = aggregatorService.aggregateMeals(meals);

        GroceryList list = new GroceryList(OffsetDateTime.now(), neededIngredientsForMeals);

        groceryListRepository.createForUser(list, user);
    }
}
