package dev.asiglesias.infrastructure.repository.implementation;

import dev.asiglesias.domain.Recipe;
import dev.asiglesias.domain.MeasureUnit;
import dev.asiglesias.infrastructure.notion.client.NotionHttpClient;
import dev.asiglesias.infrastructure.notion.client.dto.NotionIngredient;
import dev.asiglesias.infrastructure.notion.client.dto.NotionMeal;
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
        List<Recipe> recipes = mealRepository.getMealsForUser(null);

        //Assert
        assertThat(recipes).isEmpty();
    }

    @Test
    void givenClientReturnsEmptyList_whenGetMeals_thenReturnEmptyList() {
        //Arrange
        when(httpClient.getMealsForUser()).thenReturn(Collections.emptyList());

        //Act
        List<Recipe> recipes = mealRepository.getMealsForUser(null);

        //Assert
        assertThat(recipes).isEmpty();
    }

    @Test
    void givenClientReturnsMeals_whenGetMeals_thenReturnMealsRetrievedFromClient() {
        //Arrange
        List<String> recipesIds = List.of("id1", "id2");
        NotionMeal notionMeal = new NotionMeal(recipesIds, recipesIds, 1, 1);

        when(httpClient.getMealsForUser()).thenReturn(List.of(notionMeal));
        when(httpClient.getIngredientsForRecipe(anyString()))
                .thenReturn(List.of(new NotionIngredient("120g", "Rice")));

        //Act
        List<Recipe> recipes = mealRepository.getMealsForUser(null);

        //Assert
        assertThat(recipes).isNotEmpty().hasSize(2);
        assertThat(recipes).allMatch((meal) ->
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
        NotionMeal notionMeal = new NotionMeal(recipesIds, recipesIds, 1, 1);

        when(httpClient.getMealsForUser()).thenReturn(List.of(notionMeal));
        when(httpClient.getIngredientsForRecipe(anyString()))
                .thenReturn(List.of(new NotionIngredient(null, "Rice")));

        //Act
        List<Recipe> recipes = mealRepository.getMealsForUser(null);

        //Assert
        assertThat(recipes).isNotEmpty().hasSize(1);
        Recipe recipe = recipes.get(0);
        assertThat(recipe.getIngredients())
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
        NotionMeal notionMeal = new NotionMeal(recipesIds, recipesIds, 1, 1);

        when(httpClient.getMealsForUser()).thenReturn(List.of(notionMeal));
        when(httpClient.getIngredientsForRecipe(anyString()))
                .thenReturn(List.of(
                        new NotionIngredient("120g", name),
                        new NotionIngredient("120g", "Rice")
                ));

        //Act
        List<Recipe> recipes = mealRepository.getMealsForUser(null);

        //Assert
        assertThat(recipes).isNotEmpty().hasSize(1);
        Recipe recipe = recipes.get(0);
        assertThat(recipe.getIngredients())
                .hasSize(1)
                .allMatch((i) -> i.getProduct().getName().equals("Rice"));
    }

    @Test
    void givenClientReturnsMealsThatHasIngredientWithoutUnit_whenGetMeals_thenSetPieceAsDefault() {
        //Arrange
        List<String> recipesIds = List.of("id1");
        NotionMeal notionMeal = new NotionMeal(recipesIds, recipesIds, 1, 1);

        when(httpClient.getMealsForUser()).thenReturn(List.of(notionMeal));
        when(httpClient.getIngredientsForRecipe(anyString()))
                .thenReturn(List.of(new NotionIngredient("120", "Rice")));

        //Act
        List<Recipe> recipes = mealRepository.getMealsForUser(null);

        //Assert
        assertThat(recipes).isNotEmpty().hasSize(1);
        Recipe recipe = recipes.get(0);
        assertThat(recipe.getIngredients())
                .hasSize(1)
                .allMatch((i) -> i.getUnit().equals(MeasureUnit.piece()));
    }

    @Test
    void givenClientReturnsMealsWithoutIngredients_whenGetMeals_thenIgnoreMeals() {
        //Arrange
        List<String> recipesIds = Collections.emptyList();
        NotionMeal notionMeal = new NotionMeal(recipesIds, recipesIds, 1, 1);

        when(httpClient.getMealsForUser()).thenReturn(List.of(notionMeal));

        //Act
        List<Recipe> recipes = mealRepository.getMealsForUser(null);

        //Assert
        verify(httpClient, never()).getIngredientsForRecipe(anyString());
        assertThat(recipes).isEmpty();
    }
}