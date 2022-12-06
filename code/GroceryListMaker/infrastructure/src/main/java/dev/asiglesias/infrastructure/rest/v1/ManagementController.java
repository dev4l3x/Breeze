package dev.asiglesias.infrastructure.rest.v1;

import dev.asiglesias.application.GenerateGroceryListUseCase;
import dev.asiglesias.application.GenerateGroceryListUseCaseImpl;
import dev.asiglesias.domain.repository.GroceryListRepository;
import dev.asiglesias.domain.repository.MealRepository;
import dev.asiglesias.domain.service.IngredientAggregatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("grocery-list")
public class ManagementController {

    private final GenerateGroceryListUseCase generateGroceryListUseCase;

    public ManagementController(MealRepository mealRepository, GroceryListRepository groceryListRepository) {

        IngredientAggregatorService aggregatorService = new IngredientAggregatorService();
        generateGroceryListUseCase =
                new GenerateGroceryListUseCaseImpl(aggregatorService, mealRepository, groceryListRepository);
    }

    @PostMapping("generate")
    public ResponseEntity<Void> generate() {
        generateGroceryListUseCase.generate();
        return ResponseEntity.noContent().build();
    }
}
