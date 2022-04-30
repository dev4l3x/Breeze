package dev.asiglesias.infrastructure;

import dev.asiglesias.application.GenerateGroceryListUseCase;
import dev.asiglesias.application.GenerateGroceryListUseCaseImpl;
import dev.asiglesias.domain.Meal;
import dev.asiglesias.domain.repository.GroceryListRepository;
import dev.asiglesias.domain.repository.MealRepository;
import dev.asiglesias.domain.service.IngredientAggregatorService;
import dev.asiglesias.infrastructure.repository.implementation.NotionMealRepository;
import dev.asiglesias.infrastructure.rest.client.notion.NotionHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class Application implements CommandLineRunner {

    GenerateGroceryListUseCase generateGroceryListUseCase;

    public Application(MealRepository mealRepository, GroceryListRepository groceryListRepository) {
        IngredientAggregatorService aggregatorService = new IngredientAggregatorService();


        generateGroceryListUseCase = new GenerateGroceryListUseCaseImpl(aggregatorService, mealRepository, groceryListRepository);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        generateGroceryListUseCase.generate();
    }
}
