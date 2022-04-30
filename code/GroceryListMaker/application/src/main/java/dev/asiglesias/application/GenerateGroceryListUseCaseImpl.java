package dev.asiglesias.application;

import dev.asiglesias.domain.Ingredient;
import dev.asiglesias.domain.Meal;
import dev.asiglesias.domain.repository.GroceryListRepository;
import dev.asiglesias.domain.repository.MealRepository;
import dev.asiglesias.domain.service.IngredientAggregatorService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GenerateGroceryListUseCaseImpl implements GenerateGroceryListUseCase {

    private final IngredientAggregatorService aggregatorService;

    private final MealRepository mealRepository;

    private final GroceryListRepository groceryListRepository;

    @Override
    public void generate() {

        List<Meal> meals = mealRepository.getMeals();

        List<Ingredient> allIngredients = meals.stream()
                .flatMap(meal -> meal.getIngredients().stream())
                .collect(Collectors.toList());

        List<Ingredient> aggregatedIngredients = aggregatorService.aggregate(allIngredients);

        groceryListRepository.create(aggregatedIngredients);
    }
}
