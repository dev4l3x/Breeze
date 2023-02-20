package dev.asiglesias.infrastructure.notion.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotionIngredient {
    private String quantity;
    private String name;
}
