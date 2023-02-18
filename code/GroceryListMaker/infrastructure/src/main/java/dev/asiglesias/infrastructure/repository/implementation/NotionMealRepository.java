package dev.asiglesias.infrastructure.repository.implementation;

import dev.asiglesias.domain.*;
import dev.asiglesias.domain.repository.MealRepository;
import dev.asiglesias.infrastructure.rest.client.notion.NotionHttpClient;
import dev.asiglesias.infrastructure.rest.client.notion.dto.NotionIngredient;
import dev.asiglesias.infrastructure.rest.client.notion.dto.NotionMeal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class NotionMealRepository implements MealRepository {

    private final NotionHttpClient notionHttpClient;

    @Override
    public List<Meal> getMealsForUser(User user) {
        List<NotionMeal> meals = notionHttpClient.getMealsForUser(user.getUsername());

        if (Objects.isNull(meals)) {
            return Collections.emptyList();
        }

        //We get the ingredients with distinct here to save request
        Map<String, List<NotionIngredient>> ingredientsByRecipe = meals.stream()
            .flatMap(meal -> meal.getRecipeIds().stream()).distinct()
            .map((recipeId) -> {
                List<NotionIngredient> ingredients = notionHttpClient.getIngredientsForUser(recipeId, user.getUsername());
                int multiplyIngredientsBy = numberOfRecipesWithId(meals, recipeId);
                List<NotionIngredient> allIngredientsNeeded =
                        Collections.nCopies(multiplyIngredientsBy, ingredients).stream().flatMap(List::stream).collect(Collectors.toList());
                return Map.entry(recipeId, allIngredientsNeeded);
            })
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        return mapRecipesToDomainMeals(ingredientsByRecipe);
    }

    private int numberOfRecipesWithId(List<NotionMeal> meals, String recipeId) {
        return (int) meals.stream()
                .flatMap(meal -> meal.getRecipeIds().stream())
                .filter(recipeId::equals).count();
    }

    private List<Meal> mapRecipesToDomainMeals(Map<String, List<NotionIngredient>> ingredientsByRepice) {
        return ingredientsByRepice.values().stream()
            .map((ingredients) ->
                    ingredients.stream()
                            .filter(ingredient -> Objects.nonNull(ingredient.getName()) && !ingredient.getName().isBlank())
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
        if (Objects.isNull(quantity)) {
            return OptionalDouble.empty();
        }
        String unitToRemove = getUnitFromQuantity(quantity).orElse("");
        String quantityWithoutUnit = quantity.replace(unitToRemove, "");
        return quantity.isBlank() ? OptionalDouble.empty() : OptionalDouble.of(Double.parseDouble(quantityWithoutUnit));
    }

    private Optional<String> getUnitFromQuantity(String quantity) {
        if (Objects.isNull(quantity)) {
            return Optional.empty();
        }
        Pattern unitPattern = Pattern.compile("([a-zA-Z]+)");
        Matcher m = unitPattern.matcher(quantity);
        return m.find() ? Optional.ofNullable(m.group(0)) : Optional.empty();
    }

}
