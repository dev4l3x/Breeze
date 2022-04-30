package dev.asiglesias.infrastructure.rest.client.notion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NotionMeal {
    List<String> recipeIds;
}
