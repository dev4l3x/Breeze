package dev.asiglesias.infrastructure.grocerylist.controllers;

import dev.asiglesias.application.GenerateGroceryListUseCase;
import dev.asiglesias.application.GenerateGroceryListUseCaseImpl;
import dev.asiglesias.application.auth.repositories.UserRepository;
import dev.asiglesias.application.auth.services.AuthenticationContext;
import dev.asiglesias.domain.repository.GroceryListRepository;
import dev.asiglesias.domain.repository.MealRepository;
import dev.asiglesias.domain.service.IngredientAggregatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("grocery-list")
public class GroceryListController {

    private final GenerateGroceryListUseCase generateGroceryListUseCase;


    public GroceryListController(MealRepository mealRepository, GroceryListRepository groceryListRepository,
                                 UserRepository userRepository, AuthenticationContext authenticationContext) {

        IngredientAggregatorService aggregatorService = new IngredientAggregatorService();
        generateGroceryListUseCase =
                new GenerateGroceryListUseCaseImpl(aggregatorService, mealRepository, groceryListRepository, authenticationContext, userRepository);
    }

    @PostMapping("generate")
    public ResponseEntity<Void> generate() {
        generateGroceryListUseCase.generateForUser();
        return ResponseEntity.noContent().build();
    }
}
