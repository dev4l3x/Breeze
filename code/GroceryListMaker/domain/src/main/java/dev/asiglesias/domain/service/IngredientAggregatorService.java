package dev.asiglesias.domain.service;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import dev.asiglesias.domain.Ingredient;
import dev.asiglesias.domain.MeasureUnit;
import dev.asiglesias.domain.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IngredientAggregatorService {

    public List<Ingredient> aggregate(List<Ingredient> ingredients) {
        Map<Product, List<Ingredient>> ingredientsGroupedByProduct = ingredients.stream()
                .map(Ingredient::getProduct)
                .distinct()
                .collect(Collectors.toUnmodifiableMap(
                        (product -> product),
                        (product -> ingredients.stream()
                                .filter((ingredient -> ingredient.getProduct().equals(product))).collect(Collectors.toList()))
                ));


        List<Ingredient> aggregatedIngredients = new ArrayList<>(ingredientsGroupedByProduct.size());

        ingredientsGroupedByProduct.forEach((product, ingredientsForProduct) -> {
            final List<MeasureUnit> units = getAllUsedUnits(ingredientsForProduct);
            units.forEach(unit -> {
                final double quantity = ingredientsForProduct.stream()
                        .filter(ingredient -> ingredient.getUnit() == unit)
                        .map(Ingredient::getQuantity)
                        .reduce(0d, Double::sum);

                aggregatedIngredients.add(
                        Ingredient.builder()
                                .quantity(quantity)
                                .unit(unit)
                                .product(product)
                                .build()
                );
            });

        });

        return aggregatedIngredients;
    }

    private List<MeasureUnit> getAllUsedUnits(List<Ingredient> ingredients) {
        return ingredients.stream()
                .map(Ingredient::getUnit)
                .distinct()
                .collect(Collectors.toList());
    }

}
