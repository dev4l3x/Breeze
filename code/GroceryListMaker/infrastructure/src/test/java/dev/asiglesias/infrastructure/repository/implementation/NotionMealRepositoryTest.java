package dev.asiglesias.infrastructure.repository.implementation;

import dev.asiglesias.domain.Meal;
import dev.asiglesias.domain.MeasureUnit;
import dev.asiglesias.infrastructure.notion.client.NotionHttpClient;
import dev.asiglesias.infrastructure.notion.client.dto.NotionIngredient;
import dev.asiglesias.infrastructure.notion.client.dto.NotionDay;
import dev.asiglesias.infrastructure.notion.repositories.NotionMealRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotionMealRepositoryTest {

    @Mock
    private NotionHttpClient httpClient;

    @InjectMocks
    private NotionMealRepository mealRepository;

    @Test
    void whenGetMeals_AlwaysCallsNotionHttpClient() {
        //Act
        mealRepository.getMealsForUser(null);

        //Assert
        verify(httpClient).getMealsForUser();
    }

    @Test
    void givenClientReturnsNull_whenGetMeals_thenReturnEmptyList() {
        //Arrange
        when(httpClient.getMealsForUser()).thenReturn(null);

        //Act
        List<Meal> meals = mealRepository.getMealsForUser(null);

        //Assert
        assertThat(meals).isEmpty();
    }

    @Test
    void givenClientReturnsEmptyList_whenGetMeals_thenReturnEmptyList() {
        //Arrange
        when(httpClient.getMealsForUser()).thenReturn(Collections.emptyList());

        //Act
        List<Meal> meals = mealRepository.getMealsForUser(null);

        //Assert
        assertThat(meals).isEmpty();
    }

    @Test
    void givenClientReturnsMeals_whenGetMeals_thenReturnMealsRetrievedFromClient() {
        //Arrange
        List<String> recipesIds = List.of("id1", "id2");
        NotionDay notionDay = new NotionDay(recipesIds, recipesIds, 1, 1);

        when(httpClient.getMealsForUser()).thenReturn(List.of(notionDay));
        when(httpClient.getIngredientsForRecipe(anyString()))
                .thenReturn(List.of(new NotionIngredient("120g", "Rice")));

        //Act
        List<Meal> meals = mealRepository.getMealsForUser(null);

        //Assert
        assertThat(meals).isNotEmpty().hasSize(2);
        assertThat(meals).allMatch((meal) ->
                meal.getIngredients().stream().allMatch(
                        i -> i.getQuantity() == 120
                                && i.getProduct().getName().equals("Rice")
                                && i.getUnit().getUnitName().equals("g")
                )
        );
    }

    @Test
    void givenClientReturnsMealsThatHasIngredientsWithNullQuantity_whenGetMeals_thenSetQuantityToZero() {
        //Arrange
        List<String> recipesIds = List.of("id1");
        NotionDay notionDay = new NotionDay(recipesIds, recipesIds, 1, 1);

        when(httpClient.getMealsForUser()).thenReturn(List.of(notionDay));
        when(httpClient.getIngredientsForRecipe(anyString()))
                .thenReturn(List.of(new NotionIngredient(null, "Rice")));

        //Act
        List<Meal> meals = mealRepository.getMealsForUser(null);

        //Assert
        assertThat(meals).isNotEmpty().hasSize(1);
        Meal meal = meals.get(0);
        assertThat(meal.getIngredients())
                .isNotEmpty()
                .hasSize(1)
                .allMatch(i -> i.getQuantity() == 0 && i.getUnit().equals(MeasureUnit.piece()));
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    void givenClientReturnsMealsThatHasIngredientWithInvalidName_whenGetMeals_thenIgnoreIngredient(String name) {
        //Arrange
        List<String> recipesIds = List.of("id1");
        NotionDay notionDay = new NotionDay(recipesIds, recipesIds, 1, 1);

        when(httpClient.getMealsForUser()).thenReturn(List.of(notionDay));
        when(httpClient.getIngredientsForRecipe(anyString()))
                .thenReturn(List.of(
                        new NotionIngredient("120g", name),
                        new NotionIngredient("120g", "Rice")
                ));

        //Act
        List<Meal> meals = mealRepository.getMealsForUser(null);

        //Assert
        assertThat(meals).isNotEmpty().hasSize(1);
        Meal meal = meals.get(0);
        assertThat(meal.getIngredients())
                .hasSize(1)
                .allMatch((i) -> i.getProduct().getName().equals("Rice"));
    }

    @Test
    void givenClientReturnsMealsThatHasIngredientWithoutUnit_whenGetMeals_thenSetPieceAsDefault() {
        //Arrange
        List<String> recipesIds = List.of("id1");
        NotionDay notionDay = new NotionDay(recipesIds, recipesIds, 1, 1);

        when(httpClient.getMealsForUser()).thenReturn(List.of(notionDay));
        when(httpClient.getIngredientsForRecipe(anyString()))
                .thenReturn(List.of(new NotionIngredient("120", "Rice")));

        //Act
        List<Meal> meals = mealRepository.getMealsForUser(null);

        //Assert
        assertThat(meals).isNotEmpty().hasSize(1);
        Meal meal = meals.get(0);
        assertThat(meal.getIngredients())
                .hasSize(1)
                .allMatch((i) -> i.getUnit().equals(MeasureUnit.piece()));
    }

    @Test
    void givenClientReturnsMealsWithoutIngredients_whenGetMeals_thenIgnoreMeals() {
        //Arrange
        List<String> recipesIds = Collections.emptyList();
        NotionDay notionDay = new NotionDay(recipesIds, recipesIds, 1, 1);

        when(httpClient.getMealsForUser()).thenReturn(List.of(notionDay));

        //Act
        List<Meal> meals = mealRepository.getMealsForUser(null);

        //Assert
        verify(httpClient, never()).getIngredientsForRecipe(anyString());
        assertThat(meals).isEmpty();
    }
}