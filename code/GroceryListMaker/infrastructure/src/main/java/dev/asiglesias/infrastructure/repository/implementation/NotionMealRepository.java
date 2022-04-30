package dev.asiglesias.infrastructure.repository.implementation;

import dev.asiglesias.domain.Ingredient;
import dev.asiglesias.domain.Meal;
import dev.asiglesias.domain.MeasureUnit;
import dev.asiglesias.domain.Product;
import dev.asiglesias.domain.repository.MealRepository;
import dev.asiglesias.infrastructure.rest.client.notion.NotionHttpClient;
import dev.asiglesias.infrastructure.rest.client.notion.dto.NotionIngredient;
import dev.asiglesias.infrastructure.rest.client.notion.dto.NotionMeal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.OptionalDouble;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class NotionMealRepository implements MealRepository {

    private final NotionHttpClient notionHttpClient;

    @Override
    public List<Meal> getMeals() {
        List<NotionMeal> meals = notionHttpClient.getMeals();
        Map<String, List<NotionIngredient>> ingredientsByRecipe = meals.stream()
            .flatMap(meal -> meal.getRecipeIds().stream())
            .distinct()
            .map((recipeId) -> {
                List<NotionIngredient> ingredients = notionHttpClient.getIngredients(recipeId);
                return Map.entry(recipeId, ingredients);
            })
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        return mapRecipesToDomainMeals(ingredientsByRecipe);
    }

    private List<Meal> mapRecipesToDomainMeals(Map<String, List<NotionIngredient>> ingredientsByRepice) {
        return ingredientsByRepice.values().stream()
            .map((ingredients) ->
                    ingredients.stream()
                            .map(this::getIngredientFromNotionIngredient)
                            .collect(Collectors.toList())
            )
                .filter(ingredients -> !ingredients.isEmpty())
                .map(Meal::new)
                .collect(Collectors.toList());
    }

    private Ingredient getIngredientFromNotionIngredient(NotionIngredient notionIngredient) {
        Optional<MeasureUnit> measureUnit = getUnitFromQuantity(notionIngredient.getQuantity()).map(MeasureUnit::new);
        return Ingredient.builder()
                .product(new Product(notionIngredient.getName()))
                .quantity(getQuantityAsNumber(notionIngredient.getQuantity()).orElse(0))
                .unit(measureUnit.orElse(MeasureUnit.piece()))
                .build();
    }

    private OptionalDouble getQuantityAsNumber(String quantity) {
        String unitToRemove = getUnitFromQuantity(quantity).orElse("");
        String quantityWithoutUnit = quantity.replace(unitToRemove, "");
        return quantity.isBlank() ? OptionalDouble.empty() : OptionalDouble.of(Double.parseDouble(quantityWithoutUnit));
    }

    private Optional<String> getUnitFromQuantity(String quantity) {
        Pattern unitPattern = Pattern.compile("([a-zA-Z]+)");
        Matcher m = unitPattern.matcher(quantity);
        return m.find() ? Optional.ofNullable(m.group(0)) : Optional.empty();
    }

}
