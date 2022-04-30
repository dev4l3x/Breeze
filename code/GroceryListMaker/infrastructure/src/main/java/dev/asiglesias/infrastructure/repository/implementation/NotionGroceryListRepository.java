package dev.asiglesias.infrastructure.repository.implementation;

import dev.asiglesias.domain.Ingredient;
import dev.asiglesias.domain.repository.GroceryListRepository;
import dev.asiglesias.infrastructure.rest.client.notion.NotionHttpClient;
import dev.asiglesias.infrastructure.rest.client.notion.dto.NotionGroceryPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class NotionGroceryListRepository implements GroceryListRepository {

    private final NotionHttpClient httpClient;

    @Override
    public void create(List<Ingredient> ingredients) {
        List<String> ingredientsToSave = ingredients.stream()
                .map(Ingredient::toString)
                .collect(Collectors.toList());

        NotionGroceryPage page = new NotionGroceryPage(ingredientsToSave);

        httpClient.createGroceryListPage(page);
    }
}
