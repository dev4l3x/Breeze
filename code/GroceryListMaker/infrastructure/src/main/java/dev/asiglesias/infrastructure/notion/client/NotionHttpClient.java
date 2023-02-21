package dev.asiglesias.infrastructure.notion.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.asiglesias.application.auth.services.AuthenticationContext;
import dev.asiglesias.application.auth.services.EncryptionService;
import dev.asiglesias.infrastructure.notion.client.dto.*;
import dev.asiglesias.infrastructure.notion.client.dto.recipe.Recipe;
import dev.asiglesias.infrastructure.notion.client.dto.recipe.RichText;
import dev.asiglesias.infrastructure.notion.controllers.repositories.NotionConfigurationMongoRepository;
import dev.asiglesias.infrastructure.notion.controllers.repositories.entities.NotionConfiguration;
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
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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

    private final AuthenticationContext authenticationContext;

    @Value("${notion.baseuri}")
    private String notionBaseUri;

    @Value("${notion.version}")
    private String notionVersion;

    @Value("${notion.ingredients-property-id}")
    private String ingredientsId;

    @Value("${notion.clientid}")
    private String clientId;

    @Value("${notion.client-secret}")
    private String clientSecret;

    public NotionHttpClient(NotionConfigurationMongoRepository notionConfigurationMongoRepository,
                            EncryptionService encryptionService, AuthenticationContext authenticationContext) {
        httpClient = HttpClient.newHttpClient();
        jsonMapper = new ObjectMapper();
        this.notionConfigurationMongoRepository = notionConfigurationMongoRepository;
        this.encryptionService = encryptionService;
        this.authenticationContext = authenticationContext;
    }

    private HttpRequest.Builder buildAuthenticatedHttpRequest(String relativePath) {
        String userId = authenticationContext.getUsername();
        Optional<NotionConfiguration> notionConfiguration = notionConfigurationMongoRepository.findByUsername(userId);
        if (notionConfiguration.isEmpty()) {
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

    @SneakyThrows
    public List<NotionMeal> getMealsForUser() {
        NotionConfiguration configuration = notionConfigurationMongoRepository
                .findByUsername(authenticationContext.getUsername()).orElseThrow();

        if (configuration.getMealPlanDatabaseId() == null) {
            NotionObject mealPage = findObjectWithName("This Week", NotionObjectType.DATABASE).orElseThrow();
            configuration.setMealPlanDatabaseId(mealPage.id());
            notionConfigurationMongoRepository.save(configuration);
        }

        HttpRequest request = buildAuthenticatedHttpRequest(String.format("databases/%s/query", configuration.getMealPlanDatabaseId()))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = performRequest(request);

        Meals meals = jsonMapper.readValue(response.body(), Meals.class);

        return meals.getResults().stream().map(result -> {
            List<String> dinnerRecipes = result.getProperties().getDinner().getRelation().stream()
                    .map(Relation::getId)
                    .collect(Collectors.toList());

            List<String> lunchRecipes = result.getProperties().getLunch().getRelation().stream()
                    .map(Relation::getId)
                    .collect(Collectors.toList());

            int dinnerServings = result.getProperties().getDinnerServings().getNumber();
            int lunchServings = result.getProperties().getLunchServings().getNumber();

            return new NotionMeal(dinnerRecipes, lunchRecipes, dinnerServings, lunchServings);
        }).collect(Collectors.toList());
    }

    @SneakyThrows
    public List<NotionIngredient> getIngredientsForRecipe(String recipeId) {
        HttpRequest request = buildAuthenticatedHttpRequest(String.format("pages/%s", recipeId))
                .GET()
                .build();

        HttpResponse<String> response = performRequest(request);

        Recipe recipe = jsonMapper.readValue(response.body(), Recipe.class);

        String concatenatedIngredients =
                recipe.getProperties().getIngredients().getRichText().stream().findAny().map(RichText::getPlainText).orElse("");

        return Arrays.stream(concatenatedIngredients.split(", "))
                .map((ingredient) -> {
                    String[] quantityAndNameIngredient = ingredient.split("-");
                    String quantity = quantityAndNameIngredient.length > 1
                            ? quantityAndNameIngredient[0] : "";
                    String name = quantityAndNameIngredient.length > 1
                            ? quantityAndNameIngredient[1] : quantityAndNameIngredient[0];
                    return new NotionIngredient(quantity, name);
                })
                .collect(Collectors.toList());

    }

    public void createGroceryListPage(NotionGroceryPage page) {
        String body = getGroceryPageAsJson(page);

        HttpRequest request = buildAuthenticatedHttpRequest("pages")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();


        HttpResponse<String> response = performRequest(request);
        HttpStatus status = HttpStatus.valueOf(response.statusCode());

        if (status.isError()) {
            log.error("Notion HTTP Client error while saving grocery list: {}", response.body());
        } else {
            log.trace("Grocery List created successfully");
        }
    }

    @SneakyThrows
    public Optional<NotionUserInfo> getAccessTokenForCode(String code) {
        ObjectNode body = jsonMapper.createObjectNode();
        body.put("grant_type", "authorization_code");
        body.put("code", code);

        String basicAuthToken = getBasicAuthenticationToken();

        HttpRequest request = buildHttpRequest("oauth/token")
                .header(AUTHORIZATION_HEADER, basicAuthToken)
                .POST(HttpRequest.BodyPublishers.ofString(jsonMapper.writeValueAsString(body)))
                .build();

        HttpResponse<String> response = performRequest(request);
        HttpStatus status = HttpStatus.valueOf(response.statusCode());

        if (status.isError()) {
            log.error("Notion HTTP Client error while retrieving access token: {}", response.body());
        }

        String accessToken = jsonMapper.readTree(response.body()).get("access_token").textValue();
        String duplicatedPageId = jsonMapper.readTree(response.body()).get("duplicated_template_id").textValue();

        return Optional.of(new NotionUserInfo(accessToken, duplicatedPageId));
    }

    private HttpResponse<String> performRequest(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            log.error("An error has occurred while performing request for {}", request.uri());
            return null;
        }
    }

    private String getBasicAuthenticationToken() {
        String basicAuthentication = String.format("%s:%s", clientId, clientSecret);
        String basicAuthToken = Base64.getEncoder()
                .encodeToString(basicAuthentication.getBytes(StandardCharsets.UTF_8));
        return "Basic " + basicAuthToken;
    }

    @SneakyThrows
    public Optional<NotionObject> findObjectWithName(String name, NotionObjectType objectType) {

        ObjectNode body = jsonMapper.createObjectNode();
        body.put("query", name);

        ObjectNode filterNode = body.putObject("filter");
        filterNode.put("value", objectType.getName());
        filterNode.put("property", "object");

        HttpRequest request = buildAuthenticatedHttpRequest("search")
                .POST(HttpRequest.BodyPublishers.ofString(jsonMapper.writeValueAsString(body)))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode page = jsonMapper.readTree(response.body()).withArray("results").iterator().next();

            String id = page.get("id").asText();

            return Optional.of(new NotionObject(id, name));

        } catch (Exception exception) {
            log.error("An error has occurred while trying to get meals");
            return Optional.empty();
        }
    }

    @SneakyThrows
    private String getGroceryPageAsJson(NotionGroceryPage page) {

        NotionConfiguration configuration = notionConfigurationMongoRepository
                .findByUsername(authenticationContext.getUsername()).orElseThrow();

        if (configuration.getGroceryListDatabaseId() == null) {
            NotionObject groceryList = findObjectWithName("Grocery List", NotionObjectType.DATABASE).orElseThrow();
            configuration.setGroceryListDatabaseId(groceryList.id());
            notionConfigurationMongoRepository.save(configuration);
        }

        ObjectNode databaseId = jsonMapper.createObjectNode();
        databaseId.put("database_id", configuration.getGroceryListDatabaseId());


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
