package dev.asiglesias.infrastructure.notion.controllers.repositories;

import dev.asiglesias.infrastructure.notion.controllers.repositories.entities.NotionConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotionConfigurationMongoRepository extends MongoRepository<NotionConfiguration, String> {
}
