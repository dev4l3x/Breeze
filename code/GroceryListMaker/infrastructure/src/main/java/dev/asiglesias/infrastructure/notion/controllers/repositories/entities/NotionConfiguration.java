package dev.asiglesias.infrastructure.notion.controllers.repositories.entities;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class NotionConfiguration {

    String username;

    String secret;
}