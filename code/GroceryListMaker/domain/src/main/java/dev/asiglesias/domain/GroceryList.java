package dev.asiglesias.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
public class GroceryList {
    User user;

    OffsetDateTime createdAt;

    List<Ingredient> ingredients;

    public boolean hasIngredients() {
        return Objects.nonNull(ingredients) && !ingredients.isEmpty();
    }

}
