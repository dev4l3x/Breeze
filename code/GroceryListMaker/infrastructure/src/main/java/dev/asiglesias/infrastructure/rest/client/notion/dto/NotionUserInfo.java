package dev.asiglesias.infrastructure.rest.client.notion.dto;

public record NotionUserInfo(String token, String duplicatedPageId, String groceryListId, String mealPlanId) {
}
