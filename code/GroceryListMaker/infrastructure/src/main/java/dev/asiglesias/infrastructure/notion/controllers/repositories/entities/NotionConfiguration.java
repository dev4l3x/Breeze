package dev.asiglesias.infrastructure.notion.controllers.repositories.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class NotionConfiguration {

    @Id
    String id;

    String username;

    String secret;

    String mealPageId;

    String groceryListDatabaseId;

    String mealPlanDatabaseId;
}
