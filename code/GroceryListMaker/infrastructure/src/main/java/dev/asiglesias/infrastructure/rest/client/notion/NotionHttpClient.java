package dev.asiglesias.infrastructure.rest.client.notion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import dev.asiglesias.infrastructure.rest.client.notion.dto.NotionIngredient;
import dev.asiglesias.infrastructure.rest.client.notion.dto.NotionMeal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


@Component
@Slf4j
public class NotionHttpClient {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String NOTION_VERSION_HEADER = "Notion-Version";

    private final HttpClient httpClient;

    private final ObjectMapper jsonMapper;

    @Value("${notion.secret}")
    private String notionSecret;

    @Value("${notion.baseuri}")
    private String notionBaseUri;

    @Value("${notion.mealdbid}")
    private String mealDatabaseId;

    @Value("${notion.version}")
    private String notionVersion;

    @Value("${notion.ingredients-property-id}")
    private String ingredientsId;

    public NotionHttpClient() {
        httpClient = HttpClient.newHttpClient();
        jsonMapper = new ObjectMapper();
    }

    private HttpRequest.Builder buildHttpRequest(String relativePath) {
        return HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%s", notionBaseUri, relativePath)))
                .header(AUTHORIZATION_HEADER, notionSecret)
                .header(NOTION_VERSION_HEADER, notionVersion);
    }

    public List<NotionMeal> getMeals() {
        HttpRequest request = buildHttpRequest(String.format("databases/%s/query", mealDatabaseId))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            Spliterator<JsonNode> results = jsonMapper.readTree(response.body()).withArray("results").spliterator();

            return StreamSupport.stream(results, false)
                    .map(page -> page.findValue("properties"))
                    .map(pageProperties -> {
                        ArrayNode dinnerRecipesIds =
                                pageProperties.get("Dinner").withArray("relation");
                        ArrayNode lunchRecipesIds =
                                pageProperties.findValue("Lunch").withArray("relation");

                        dinnerRecipesIds.addAll(lunchRecipesIds);

                        return dinnerRecipesIds;
                    })
                    .map(mealRecipeNodes -> {
                        List<String> mealRecipeIds = StreamSupport.stream(mealRecipeNodes.spliterator(), false)
                                .filter(node -> node.has("id"))
                                .map((node) -> node.get("id").asText()).collect(Collectors.toList());
                        return new NotionMeal(mealRecipeIds);
                    })
                    .filter((notionMeal -> !notionMeal.getRecipeIds().isEmpty()))
                    .collect(Collectors.toList());


        } catch (Exception exception) {
            log.error("An error has occurred while trying to get meals");
            return Collections.emptyList();
        }
    }

    public List<NotionIngredient> getIngredients(String recipeId) {
        HttpRequest request = buildHttpRequest(String.format("pages/%s/properties/%s", recipeId, ingredientsId))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            String results = jsonMapper.readTree(response.body()).withArray("results").get(0)
                    .get("rich_text").get("plain_text").asText();

            return Stream.of(results)
                    .filter(ingredients -> !ingredients.isBlank())
                    .flatMap((ingredients) -> Arrays.stream(ingredients.split(", ")))
                    .map((ingredient) -> {
                        String[] quantityAndNameIngredient = ingredient.split("-");
                        String quantity = quantityAndNameIngredient.length > 1
                                ? quantityAndNameIngredient[0] : "";
                        String name = quantityAndNameIngredient.length > 1
                                ? quantityAndNameIngredient[1] : quantityAndNameIngredient[0];
                        return new NotionIngredient(quantity, name);
                    })
                    .collect(Collectors.toList());


        } catch (Exception exception) {
            log.error("An error has occurred while trying to get meals");
            log.error(exception.toString());
            return Collections.emptyList();
        }
    }

}
