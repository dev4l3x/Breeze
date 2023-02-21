package dev.asiglesias.application;

import dev.asiglesias.application.auth.repositories.UserRepository;
import dev.asiglesias.application.auth.services.AuthenticationContext;
import dev.asiglesias.domain.GroceryList;
import dev.asiglesias.domain.Ingredient;
import dev.asiglesias.domain.Meal;
import dev.asiglesias.domain.User;
import dev.asiglesias.domain.exception.DomainException;
import dev.asiglesias.domain.repository.GroceryListRepository;
import dev.asiglesias.domain.repository.MealRepository;
import dev.asiglesias.domain.service.IngredientAggregatorService;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class GenerateGroceryListUseCaseImpl implements GenerateGroceryListUseCase {

    private final IngredientAggregatorService aggregatorService;

    private final MealRepository mealRepository;

    private final GroceryListRepository groceryListRepository;

    private final AuthenticationContext authenticationContext;

    private final UserRepository userRepository;

    @Override
    public void generateForUser() {
        String authenticatedUser = authenticationContext.getUsername();

        Optional<User> user = userRepository.findByUsername(authenticatedUser);

        if(user.isEmpty()) {
            throw new DomainException("USER_NOT_FOUND", String.format("User %s not found", authenticatedUser));
        }

        List<Meal> meals = mealRepository.getMealsForUser(user.get());

        List<Ingredient> neededIngredientsForMeals = aggregatorService.aggregateMeals(meals);

        GroceryList list = new GroceryList(OffsetDateTime.now(), neededIngredientsForMeals);

        groceryListRepository.create(list);
    }
}
