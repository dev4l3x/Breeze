package dev.asiglesias.infrastructure.notion.controllers.repositories;

import dev.asiglesias.infrastructure.notion.controllers.repositories.entities.NotionConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface NotionConfigurationMongoRepository extends CrudRepository<NotionConfiguration, String> {
    Optional<NotionConfiguration> findByUsername(String username);
    boolean existsByUsername(String username);
}
