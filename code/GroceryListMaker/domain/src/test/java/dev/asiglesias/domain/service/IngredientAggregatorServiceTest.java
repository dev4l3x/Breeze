package dev.asiglesias.domain.service;

import dev.asiglesias.domain.Ingredient;
import dev.asiglesias.domain.MeasureUnit;
import dev.asiglesias.domain.Product;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


class IngredientAggregatorServiceTest {

    IngredientAggregatorService aggregatorService = new IngredientAggregatorService();

    @Test
    void givenIngredientsWithSameProduct_whenAggregate_thenReturnOneProductWithAggregatedQuantities() {
        Product product = new Product("tomato", Collections.emptyList(), MeasureUnit.PIECE);
        Ingredient firstTomato = new Ingredient(1, MeasureUnit.PIECE, product);
        Ingredient secondTomato = new Ingredient(1, MeasureUnit.PIECE, product);

        List<Ingredient> aggregatedTomatoes = aggregatorService.aggregate(List.of(firstTomato, secondTomato));

        assertThat(aggregatedTomatoes).hasSize(1);
        assertEquals(2, aggregatedTomatoes.get(0).getQuantity());
        assertEquals(product, aggregatedTomatoes.get(0).getProduct());
    }

    @Test
    void givenIngredientsWithSameProductButDifferentUnit_whenAggregate_thenOnlyAggregateSameUnit() {
        Product product = new Product("tomato", Collections.emptyList(), MeasureUnit.PIECE);
        Ingredient firstTomato = new Ingredient(1, MeasureUnit.PIECE, product);
        Ingredient secondTomato = new Ingredient(1, MeasureUnit.PIECE, product);
        Ingredient tomatoWithDifferentUnit = new Ingredient(100, MeasureUnit.GRAMS, product);

        List<Ingredient> aggregatedTomatoes =
                aggregatorService.aggregate(List.of(firstTomato, secondTomato, tomatoWithDifferentUnit));

        assertThat(aggregatedTomatoes).hasSize(1);
        assertEquals(2, aggregatedTomatoes.get(0).getQuantity());
        assertEquals(product, aggregatedTomatoes.get(0).getProduct());
    }

}