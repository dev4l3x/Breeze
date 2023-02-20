package dev.asiglesias.infrastructure.notion.repositories;

import dev.asiglesias.domain.GroceryList;
import dev.asiglesias.domain.Ingredient;
import dev.asiglesias.domain.User;
import dev.asiglesias.domain.repository.GroceryListRepository;
import dev.asiglesias.infrastructure.notion.client.NotionHttpClient;
import dev.asiglesias.infrastructure.notion.client.dto.NotionGroceryPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class NotionGroceryListRepository implements GroceryListRepository {

    private final NotionHttpClient httpClient;

    @Override
    public void createForUser(GroceryList groceryList, User user) {

        if (!groceryList.hasIngredients()) {
            log.warn("Not saving grocery list because ingredients is null or empty");
            return;
        }

        List<String> ingredientsToSave = groceryList.getIngredients().stream()
                .map(Ingredient::toString)
                .collect(Collectors.toList());

        NotionGroceryPage page = new NotionGroceryPage(ingredientsToSave);

        httpClient.createGroceryListPage(page);
    }
}
