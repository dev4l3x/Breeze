package dev.asiglesias.infrastructure.rest.client.notion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotionIngredient {
    private String quantity;
    private String name;
}
