package dev.asiglesias.infrastructure.rest.v1;

import dev.asiglesias.application.GenerateGroceryListUseCase;
import dev.asiglesias.application.GenerateGroceryListUseCaseImpl;
import dev.asiglesias.application.auth.repositories.UserRepository;
import dev.asiglesias.domain.User;
import dev.asiglesias.domain.repository.GroceryListRepository;
import dev.asiglesias.domain.repository.MealRepository;
import dev.asiglesias.domain.service.IngredientAggregatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("grocery-list")
public class ManagementController {

    private final GenerateGroceryListUseCase generateGroceryListUseCase;

    private final UserRepository userRepository;

    public ManagementController(MealRepository mealRepository, GroceryListRepository groceryListRepository,
                                UserRepository userRepository) {

        IngredientAggregatorService aggregatorService = new IngredientAggregatorService();
        generateGroceryListUseCase =
                new GenerateGroceryListUseCaseImpl(aggregatorService, mealRepository, groceryListRepository);
        this.userRepository = userRepository;
    }

    @PostMapping("generate")
    public ResponseEntity<Void> generate() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<User> user = userRepository.findByUsername(username);

        if(user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        generateGroceryListUseCase.generateForUser(user.get());
        return ResponseEntity.noContent().build();
    }
}
