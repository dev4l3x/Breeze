package dev.asiglesias.application;

import dev.asiglesias.application.auth.repositories.UserRepository;
import dev.asiglesias.application.auth.services.AuthenticationContext;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerateGroceryListUseCaseTest {

    public static final String TEST_USER = "test-user";
    @Mock
    private IngredientAggregatorService aggregatorService;

    @Mock
    private MealRepository mealRepository;

    @Mock
    private GroceryListRepository groceryListRepository;

    @Mock
    private AuthenticationContext authenticationContext;

    @Mock
    private UserRepository userRepository;

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

        Meal mealWithOneTomato = new Meal(Collections.singletonList(oneTomato), 1);
        Meal mealWithTwoTomatoes = new Meal(Collections.singletonList(twoTomatoes), 1);

        User user = new User("test", "test");

        when(mealRepository.getMealsForUser(user)).thenReturn(List.of(mealWithOneTomato, mealWithTwoTomatoes));
        when(aggregatorService.aggregateMeals(anyList())).thenReturn(List.of(aggregatedTomato));
        when(authenticationContext.getUsername()).thenReturn(TEST_USER);
        when(userRepository.findByUsername(TEST_USER)).thenReturn(Optional.of(user));

        //Act
        useCase.generateForUser();

        //Assert
        verify(mealRepository).getMealsForUser(user);
        verify(aggregatorService).aggregateMeals(
               argThat(meals -> meals.contains(mealWithOneTomato) && meals.contains(mealWithTwoTomatoes))
        );
        ArgumentCaptor<GroceryList> groceryListCaptor = ArgumentCaptor.forClass(GroceryList.class);
        verify(groceryListRepository).create(groceryListCaptor.capture());
        GroceryList groceryList = groceryListCaptor.getValue();
        assertThat(groceryList.getIngredients()).containsOnly(aggregatedTomato);
    }

}