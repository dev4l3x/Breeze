package dev.asiglesias.application;

import dev.asiglesias.domain.*;
import dev.asiglesias.domain.repository.GroceryListRepository;
import dev.asiglesias.domain.repository.MealRepository;
import dev.asiglesias.domain.service.IngredientAggregatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
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

        User user = new User("test");

        when(mealRepository.getMealsForUser(user)).thenReturn(List.of(mealWithOneTomato, mealWithTwoTomatoes));
        when(aggregatorService.aggregate(anyList())).thenReturn(List.of(aggregatedTomato));

        //Act
        useCase.generateForUser(user);

        //Assert
        verify(mealRepository).getMealsForUser(user);
        verify(aggregatorService).aggregate(
               argThat(ingredients -> ingredients.contains(oneTomato) && ingredients.contains(twoTomatoes))
        );
        ArgumentCaptor<GroceryList> groceryListCaptor = ArgumentCaptor.forClass(GroceryList.class);
        verify(groceryListRepository).createForUser(groceryListCaptor.capture(), eq(user));
        GroceryList groceryList = groceryListCaptor.getValue();
        assertThat(groceryList.getIngredients()).containsOnly(aggregatedTomato);
    }

}