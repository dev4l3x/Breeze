package dev.asiglesias.infrastructure.notion.client.dto;

public record NotionUserInfo(String token, String duplicatedPageId, String groceryListId, String mealPlanId) {
}
