package dev.asiglesias.domain.service;

import dev.asiglesias.domain.Ingredient;
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
                                .filter((ingredient -> ingredient.getUnit() == product.getPreferredUnit()))
                                .filter((ingredient -> ingredient.getProduct().equals(product))).collect(Collectors.toList()))
                ));


        List<Ingredient> aggregatedIngredients = new ArrayList<>(ingredientsGroupedByProduct.size());

        ingredientsGroupedByProduct.forEach((product, ingredientsForProduct) -> {
            final float quantity = ingredientsForProduct.stream()
                    .map(Ingredient::getQuantity)
                    .reduce(0f, Float::sum);
            aggregatedIngredients.add(new Ingredient(quantity, product.getPreferredUnit(), product));
        });

        return aggregatedIngredients;
    }

}
