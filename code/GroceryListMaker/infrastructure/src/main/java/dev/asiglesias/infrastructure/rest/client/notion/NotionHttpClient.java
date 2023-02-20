package dev.asiglesias.infrastructure.rest.client.notion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.asiglesias.application.auth.services.EncryptionService;
import dev.asiglesias.infrastructure.notion.controllers.repositories.NotionConfigurationMongoRepository;
import dev.asiglesias.infrastructure.notion.controllers.repositories.entities.NotionConfiguration;
import dev.asiglesias.infrastructure.rest.client.notion.dto.NotionGroceryPage;
import dev.asiglesias.infrastructure.rest.client.notion.dto.NotionIngredient;
import dev.asiglesias.infrastructure.rest.client.notion.dto.NotionMeal;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


@Component
@Slf4j
public class NotionHttpClient {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String NOTION_VERSION_HEADER = "Notion-Version";
    public static final String CONTENT_TYPE = "Content-Type";

    private final HttpClient httpClient;

    private final ObjectMapper jsonMapper;

    private final NotionConfigurationMongoRepository notionConfigurationMongoRepository;

    private final EncryptionService encryptionService;

    @Value("${notion.baseuri}")
    private String notionBaseUri;

    @Value("${notion.mealdbid}")
    private String mealDatabaseId;

    @Value("${notion.version}")
    private String notionVersion;

    @Value("${notion.ingredients-property-id}")
    private String ingredientsId;

    @Value("${notion.grocerylistdb}")
    private String groceryListId;

    @Value("${notion.clientid}")
    private String clientId;

    @Value("${notion.client-secret}")
    private String clientSecret;

    public NotionHttpClient(NotionConfigurationMongoRepository notionConfigurationMongoRepository,
                            EncryptionService encryptionService) {
        httpClient = HttpClient.newHttpClient();
        jsonMapper = new ObjectMapper();
        this.notionConfigurationMongoRepository = notionConfigurationMongoRepository;
        this.encryptionService = encryptionService;
    }

    private HttpRequest.Builder buildAuthenticatedHttpRequest(String relativePath, String userId) {
        Optional<NotionConfiguration> notionConfiguration = notionConfigurationMongoRepository.findByUsername(userId);
        if(notionConfiguration.isEmpty()) {
            throw new RuntimeException("No notion configuration for user " + userId);
        }
        String decryptedToken = encryptionService.decrypt(notionConfiguration.get().getSecret());

        return buildHttpRequest(relativePath)
                .header(AUTHORIZATION_HEADER, decryptedToken);
    }

    private HttpRequest.Builder buildHttpRequest(String relativePath) {
        return HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%s", notionBaseUri, relativePath)))
                .header(NOTION_VERSION_HEADER, notionVersion)
                .header(CONTENT_TYPE, "application/json");
    }

    public List<NotionMeal> getMealsForUser(String username) {
        HttpRequest request = buildAuthenticatedHttpRequest(String.format("databases/%s/query", mealDatabaseId), username)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            Spliterator<JsonNode> results = jsonMapper.readTree(response.body()).withArray("results").spliterator();

            return StreamSupport.stream(results, false)
                    .map(page -> page.findValue("properties"))
                    .map(pageProperties -> {

                        ArrayNode recipesIds = jsonMapper.createArrayNode();

                        ArrayNode dinnerRecipesIds =
                                pageProperties.get("Dinner").withArray("relation");
                        ArrayNode lunchRecipesIds =
                                pageProperties.findValue("Lunch").withArray("relation");

                        int dinnerMealQuantity =
                                pageProperties.get("Dinner Quantity").get("number").asInt(1);
                        int lunchMealQuantity =
                                pageProperties.get("Lunch Quantity").get("number").asInt(1);

                        IntStream.range(0, dinnerMealQuantity).forEach((i) -> recipesIds.addAll(dinnerRecipesIds));
                        IntStream.range(0, lunchMealQuantity).forEach((i) -> recipesIds.addAll(lunchRecipesIds));

                        return recipesIds;
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

    public List<NotionIngredient> getIngredientsForUser(String recipeId, String username) {
        HttpRequest request = buildAuthenticatedHttpRequest(String.format("pages/%s/properties/%s", recipeId, ingredientsId),
                username)
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

    public void createGroceryListPage(NotionGroceryPage page, String username) {

        String body = getGroceryPageAsJson(page);

        HttpRequest request = buildAuthenticatedHttpRequest("pages", username)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            HttpStatus status = HttpStatus.valueOf(response.statusCode());

            if (status.isError()) {
                log.error("Notion HTTP Client error while saving grocery list: {}", response.body());
            } else {
                log.trace("Grocery List created successfully");
            }

        } catch (Exception exception) {
            log.error("An error has occurred while trying to save grocery list, Error: {}", exception.toString());
        }

    }

    @SneakyThrows
    public String getAccessTokenForCode(String code) {
        ObjectNode body = jsonMapper.createObjectNode();
        body.put("grant_type", "authorization_code");
        body.put("code", code);

        String basicAuthentication = String.format("%s:%s", clientId, clientSecret);
        String basicAuthToken = Base64.getEncoder()
                .encodeToString(basicAuthentication.getBytes(StandardCharsets.UTF_8));

        HttpRequest request = buildHttpRequest("oauth/token")
                .header(AUTHORIZATION_HEADER, "Basic " + basicAuthToken)
                .POST(HttpRequest.BodyPublishers.ofString(jsonMapper.writeValueAsString(body)))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            HttpStatus status = HttpStatus.valueOf(response.statusCode());

            if (status.isError()) {
                log.error("Notion HTTP Client error while retrieving access token: {}", response.body());
            }

            return jsonMapper.readTree(response.body()).get("access_token").textValue();
        } catch (Exception ex) {
            log.error("An error has occurred while trying to obtain access token");
        }
        return null;
    }

    @SneakyThrows
    private String getGroceryPageAsJson(NotionGroceryPage page) {

        ObjectNode databaseId = jsonMapper.createObjectNode();
        databaseId.put("database_id", groceryListId);


        ObjectNode dateNode = getDatePropertyNode(page.getDate().format(DateTimeFormatter.ISO_DATE));

        ArrayNode childrenNode = jsonMapper.createArrayNode();
        page.getToBuyIngredients().forEach((ingredient) -> {
            childrenNode.add(getChecklistItemWithContent(ingredient));
        });

        ObjectNode pageNode = jsonMapper.createObjectNode();
        pageNode.set("parent", databaseId);
        pageNode.set("properties", dateNode);
        pageNode.set("children", childrenNode);

        return jsonMapper.writeValueAsString(pageNode);
    }

    private ObjectNode getDatePropertyNode(String date) {
        ObjectNode dateNode = jsonMapper.createObjectNode();
        dateNode.put("content", date);

        ObjectNode contentNode = jsonMapper.createObjectNode();
        contentNode.set("text", dateNode);

        ArrayNode titleNode = jsonMapper.createArrayNode();
        titleNode.add(contentNode);

        ObjectNode mainDatePropertyNode = jsonMapper.createObjectNode();
        mainDatePropertyNode.set("title", titleNode);

        ObjectNode propertyNode = jsonMapper.createObjectNode();
        propertyNode.set("Date", mainDatePropertyNode);

        return propertyNode;
    }

    private ObjectNode getChecklistItemWithContent(String content) {
        ObjectNode contentNode = jsonMapper.createObjectNode();
        contentNode.put("content", content);

        ObjectNode textNode = jsonMapper.createObjectNode();
        textNode.put("type", "text");
        textNode.set("text", contentNode);

        ArrayNode richTextNode = jsonMapper.createArrayNode();
        richTextNode.add(textNode);

        ObjectNode todoItemContent = jsonMapper.createObjectNode();
        todoItemContent.set("rich_text", richTextNode);

        ObjectNode todoChild = jsonMapper.createObjectNode();
        todoChild.put("object", "block");
        todoChild.put("type", "to_do");
        todoChild.set("to_do", todoItemContent);

        return todoChild;
    }


}
