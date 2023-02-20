package dev.asiglesias.infrastructure.notion.client.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class NotionGroceryPage {

    private final LocalDate date;

    private final List<String> toBuyIngredients;

    public NotionGroceryPage(List<String> toBuyIngredients) {
        date = LocalDate.now();
        this.toBuyIngredients = toBuyIngredients;
    }
}
