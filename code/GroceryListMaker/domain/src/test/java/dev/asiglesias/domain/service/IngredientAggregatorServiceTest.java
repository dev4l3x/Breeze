package dev.asiglesias.domain.service;

import dev.asiglesias.domain.Ingredient;
import dev.asiglesias.domain.Meal;
import dev.asiglesias.domain.MeasureUnit;
import dev.asiglesias.domain.Product;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


class IngredientAggregatorServiceTest {

    IngredientAggregatorService aggregatorService = new IngredientAggregatorService();

    @Test
    void givenMealsWithQuantities_whenAggregateMeals_thenReturnAggregatedIngredients() {
        //Arrange
        Product product = new Product("tomato");
        Ingredient firstTomato = Ingredient.builder().quantity(1).unit(MeasureUnit.piece()).product(product).build();
        Ingredient secondTomato = Ingredient.builder().quantity(1).unit(MeasureUnit.piece()).product(product).build();
        Meal firstMeal = new Meal(List.of(firstTomato), 2);
        Meal secondMeal = new Meal(List.of(secondTomato), 2);

        //Act
        List<Ingredient> ingredients = aggregatorService.aggregateMeals(List.of(firstMeal, secondMeal));

        //Assert
        assertThat(ingredients).hasSize(1);
        assertEquals(4, ingredients.get(0).getQuantity());
        assertEquals(product, ingredients.get(0).getProduct());
    }

    @Test
    void givenIngredientsWithSameProduct_whenAggregate_thenReturnOneProductWithAggregatedQuantities() {
        Product product = new Product("tomato");
        Ingredient firstTomato = Ingredient.builder().quantity(1).unit(MeasureUnit.piece()).product(product).build();
        Ingredient secondTomato = Ingredient.builder().quantity(1).unit(MeasureUnit.piece()).product(product).build();

        List<Ingredient> aggregatedTomatoes = aggregatorService.aggregate(List.of(firstTomato, secondTomato));

        assertThat(aggregatedTomatoes).hasSize(1);
        assertEquals(2, aggregatedTomatoes.get(0).getQuantity());
        assertEquals(product, aggregatedTomatoes.get(0).getProduct());
    }

    @Test
    void givenIngredientsWithSameProductButDifferentUnit_whenAggregate_thenOnlyAggregateSameUnit() {
        Product product = new Product("tomato");
        Ingredient firstTomato = Ingredient.builder().quantity(1).unit(MeasureUnit.piece()).product(product).build();
        Ingredient secondTomato = Ingredient.builder().quantity(1).unit(MeasureUnit.piece()).product(product).build();
        Ingredient tomatoWithDifferentUnit = Ingredient.builder()
                .quantity(100).unit(new MeasureUnit("g")).product(product).build();

        List<Ingredient> aggregatedTomatoes =
                aggregatorService.aggregate(List.of(firstTomato, secondTomato, tomatoWithDifferentUnit));

        assertThat(aggregatedTomatoes).hasSize(2);
        assertEquals(2, aggregatedTomatoes.get(0).getQuantity());
        assertEquals(100, aggregatedTomatoes.get(1).getQuantity());
    }

    @Test
    void givenIngredientsWithZeroQuantity_whenAggregate_thenResultIngredientHasZeroQuantity() {
        Product product = new Product("tomato");
        Ingredient firstTomato = Ingredient.builder().quantity(0).unit(MeasureUnit.piece()).product(product).build();
        Ingredient secondTomato = Ingredient.builder().quantity(0).unit(MeasureUnit.piece()).product(product).build();

        List<Ingredient> aggregatedTomatoes =
                aggregatorService.aggregate(List.of(firstTomato, secondTomato));

        assertThat(aggregatedTomatoes).hasSize(1);
        assertEquals(0, aggregatedTomatoes.get(0).getQuantity());
    }

    @Test
    void givenIngredientsWithDecimalQuantity_whenAggregate_thenRoundUpper() {
        Product product = new Product("tomato");
        Ingredient firstTomato = Ingredient.builder().quantity(0.5).unit(MeasureUnit.piece()).product(product).build();
        Ingredient secondTomato = Ingredient.builder().quantity(3).unit(MeasureUnit.piece()).product(product).build();

        List<Ingredient> aggregatedTomatoes =
                aggregatorService.aggregate(List.of(firstTomato, secondTomato));

        assertThat(aggregatedTomatoes).hasSize(1);
        assertEquals(4, aggregatedTomatoes.get(0).getQuantity());
    }

}