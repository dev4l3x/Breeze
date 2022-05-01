package dev.asiglesias.infrastructure.repository.implementation;

import dev.asiglesias.domain.Ingredient;
import dev.asiglesias.domain.repository.GroceryListRepository;
import dev.asiglesias.infrastructure.rest.client.notion.NotionHttpClient;
import dev.asiglesias.infrastructure.rest.client.notion.dto.NotionGroceryPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class NotionGroceryListRepository implements GroceryListRepository {

    private final NotionHttpClient httpClient;

    @Override
    public void create(List<Ingredient> ingredients) {

        if (Objects.isNull(ingredients) || ingredients.isEmpty()) {
            log.error("Not saving grocery list because ingredients is null or empty");
            return;
        }

        List<String> ingredientsToSave = ingredients.stream()
                .map(Ingredient::toString)
                .collect(Collectors.toList());

        NotionGroceryPage page = new NotionGroceryPage(ingredientsToSave);

        httpClient.createGroceryListPage(page);
    }
}
