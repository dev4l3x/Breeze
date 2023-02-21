package dev.asiglesias.infrastructure.notion.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NotionDay {
    List<String> dinnerRecipes;
    List<String> lunchRecipes;
    int dinnerServings;
    int lunchServings;
}
