package dev.asiglesias.domain.service;

import dev.asiglesias.domain.Ingredient;
import dev.asiglesias.domain.MeasureUnit;
import dev.asiglesias.domain.Product;
import dev.asiglesias.domain.Meal;

import java.util.List;
import java.util.stream.Collectors;

public class IngredientAggregatorService {

    public List<Ingredient> aggregateMeals(List<Meal> meals) {
        List<Ingredient> ingredients = meals.stream()
                .flatMap(recipe -> recipe.getIngredients().stream().map(ingredient -> ingredient.multiplyQuantityBy(recipe.getServings())))
                .collect(Collectors.toList());

        return aggregate(ingredients);
    }

    public List<Ingredient> aggregate(List<Ingredient> ingredients) {
        List<Product> allProducts = getAllProductsFromIngredients(ingredients);

        return allProducts.stream()
                .flatMap((product -> aggregateIngredients(product, ingredients).stream()))
                .collect(Collectors.toList());
    }

    //It's a list because ingredients could have different units, so we only aggregate same units
    private List<Ingredient> aggregateIngredients(Product product, List<Ingredient> ingredients) {
        final List<Ingredient> ingredientsForProduct = ingredients.stream()
                .filter(ingredient -> ingredient.getProduct().equals(product))
                .collect(Collectors.toList());

        final List<MeasureUnit> units = getAllUsedUnits(ingredientsForProduct);

        return units.stream().map(unit -> {
            final double aggregatedQuantity = sumIngredientQuantityWithUnit(ingredientsForProduct, unit);
            return Ingredient.builder()
                    .unit(unit).product(product).quantity(aggregatedQuantity).build();
        }).collect(Collectors.toList());
    }

    private double sumIngredientQuantityWithUnit(List<Ingredient> ingredients, MeasureUnit unit) {
        return Math.ceil(ingredients.stream()
                .filter(ingredient -> ingredient.getUnit().equals(unit))
                .mapToDouble(Ingredient::getQuantity)
                .sum());
    }

    private List<Product> getAllProductsFromIngredients(List<Ingredient> ingredients) {
        return ingredients.stream()
                .map(Ingredient::getProduct)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<MeasureUnit> getAllUsedUnits(List<Ingredient> ingredients) {
        return ingredients.stream()
                .map(Ingredient::getUnit)
                .distinct()
                .collect(Collectors.toList());
    }

}
