package dev.asiglesias.infrastructure.notion.repositories;

import dev.asiglesias.domain.*;
import dev.asiglesias.domain.repository.MealRepository;
import dev.asiglesias.infrastructure.notion.client.NotionHttpClient;
import dev.asiglesias.infrastructure.notion.client.dto.NotionIngredient;
import dev.asiglesias.infrastructure.notion.client.dto.NotionDay;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class NotionMealRepository implements MealRepository {

    private final NotionHttpClient notionHttpClient;

    @Override
    public List<Meal> getMealsForUser(User user) {
        List<NotionDay> days = notionHttpClient.getMealsForUser();

        if (Objects.isNull(days)) {
            return Collections.emptyList();
        }

        Map<String, List<Ingredient>> ingredientsByRecipe = getIngredientsForRecipes(days);

        Map<String, Integer> totalServingsPerRecipe = getTotalServingsPerRecipe(days);

        return mapRecipesToDomainMeals(totalServingsPerRecipe, ingredientsByRecipe);
    }

    private Map<String, Integer> getTotalServingsPerRecipe(List<NotionDay> meals) {
        Map<String, Integer> recipeServings = new HashMap<>();

        meals.forEach(meal -> {
                   meal.getDinnerRecipes().forEach(dinnerRecipe -> {
                       if (!recipeServings.containsKey(dinnerRecipe)) {
                           recipeServings.put(dinnerRecipe, 0);
                       }
                       recipeServings.put(dinnerRecipe, recipeServings.get(dinnerRecipe) + meal.getDinnerServings());
                   });
                   meal.getLunchRecipes().forEach(lunchRecipe -> {
                       if (!recipeServings.containsKey(lunchRecipe)) {
                           recipeServings.put(lunchRecipe, 0);
                       }
                       recipeServings.put(lunchRecipe, recipeServings.get(lunchRecipe) + meal.getDinnerServings());
                   });
               });
        return recipeServings;
    }

    private Map<String, List<Ingredient>> getIngredientsForRecipes(List<NotionDay> meals) {
        return meals.stream()
            .flatMap(meal -> Stream.concat(meal.getDinnerRecipes().stream(), meal.getLunchRecipes().stream())).distinct()
            .map((recipeId) -> {
                List<NotionIngredient> ingredients = notionHttpClient.getIngredientsForRecipe(recipeId)
                        .stream()
                        .filter(notionIngredient -> Objects.nonNull(notionIngredient.getName()) && !notionIngredient.getName().isBlank())
                        .collect(Collectors.toList());
                return Map.entry(recipeId, ingredients.stream().map(this::getIngredientFromNotionIngredient).collect(Collectors.toList()));
            })
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    private List<Meal> mapRecipesToDomainMeals(Map<String, Integer> recipesWithQuantity,
                                               Map<String, List<Ingredient>> ingredientsByRecipe) {
        return recipesWithQuantity.keySet().stream()
                .map(recipe -> new Meal(ingredientsByRecipe.get(recipe), recipesWithQuantity.get(recipe)))
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
