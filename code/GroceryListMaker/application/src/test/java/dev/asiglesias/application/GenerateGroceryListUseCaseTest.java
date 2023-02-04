package dev.asiglesias.application;

import dev.asiglesias.domain.Ingredient;
import dev.asiglesias.domain.Meal;
import dev.asiglesias.domain.MeasureUnit;
import dev.asiglesias.domain.Product;
import dev.asiglesias.domain.repository.GroceryListRepository;
import dev.asiglesias.domain.repository.MealRepository;
import dev.asiglesias.domain.service.IngredientAggregatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerateGroceryListUseCaseTest {

    @Mock
    private IngredientAggregatorService aggregatorService;

    @Mock
    private MealRepository mealRepository;

    @Mock
    private GroceryListRepository groceryListRepository;

    @InjectMocks
    private GenerateGroceryListUseCaseImpl useCase;

    @Test
    void givenMultipleMeals_whenGenerate_thenCallAggregateServiceWithAllIngredients() {
        //Arrange
        Product tomato = Product.builder()
                .name("Tomato")
                .build();

        Ingredient oneTomato = Ingredient.builder().quantity(1).unit(MeasureUnit.piece()).product(tomato).build();
        Ingredient twoTomatoes = Ingredient.builder().quantity(2).unit(MeasureUnit.piece()).product(tomato).build();
        Ingredient aggregatedTomato = Ingredient.builder().quantity(3).unit(MeasureUnit.piece()).product(tomato).build();

        Meal mealWithOneTomato = new Meal(Collections.singletonList(oneTomato));
        Meal mealWithTwoTomatoes = new Meal(Collections.singletonList(twoTomatoes));

        when(mealRepository.getMealsForUser(null)).thenReturn(List.of(mealWithOneTomato, mealWithTwoTomatoes));
        when(aggregatorService.aggregate(anyList())).thenReturn(List.of(aggregatedTomato));

        //Act
        useCase.generateForUser(null);

        //Assert
        verify(mealRepository).getMealsForUser(null);
        verify(aggregatorService).aggregate(
               argThat(ingredients -> ingredients.contains(oneTomato) && ingredients.contains(twoTomatoes))
        );
        verify(groceryListRepository).createForUser(null);
    }

}