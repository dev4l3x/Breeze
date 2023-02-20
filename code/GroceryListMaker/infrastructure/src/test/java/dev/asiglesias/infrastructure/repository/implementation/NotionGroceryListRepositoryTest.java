package dev.asiglesias.infrastructure.repository.implementation;

import dev.asiglesias.domain.Ingredient;
import dev.asiglesias.domain.MeasureUnit;
import dev.asiglesias.domain.Product;
import dev.asiglesias.infrastructure.rest.client.notion.NotionHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotionGroceryListRepositoryTest {

    @Mock
    private NotionHttpClient httpClient;

    @InjectMocks
    private NotionGroceryListRepository notionGroceryListRepository;

    @Test
    void givenListOfIngredients_whenCreate_thenCallHttpClientWithPage() {
        //Arrange
        Ingredient ingredient = Ingredient.builder()
                .unit(MeasureUnit.piece())
                .product(new Product("Tomato"))
                .quantity(1)
                .build();
        List<Ingredient> ingredients = List.of(ingredient);

        //Act
        notionGroceryListRepository.createForUser(null, null);

        //Assert
        verify(httpClient).createGroceryListPage(
                argThat(page -> page.getToBuyIngredients().contains(ingredient.toString()))
        );
    }

    @Test
    void givenListOfIngredientsIsNull_whenCreate_thenNotCallHttpClient() {
        //Arrange
        List<Ingredient> ingredients = null;

        //Act
        notionGroceryListRepository.createForUser(null, null);

        //Assert
        verify(httpClient, never()).createGroceryListPage(any());
    }

    @Test
    void givenListOfIngredientsEmpty_whenCreate_thenNotCallHttpClient() {
        //Arrange
        List<Ingredient> ingredients = Collections.emptyList();

        //Act
        notionGroceryListRepository.createForUser(null, null);

        //Assert
        verify(httpClient, never()).createGroceryListPage(any());
    }
}