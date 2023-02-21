package dev.asiglesias.infrastructure.notion;

import dev.asiglesias.infrastructure.notion.controllers.repositories.NotionConfigurationMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotionService {

    private final NotionConfigurationMongoRepository configurationMongoRepository;

    public boolean isNotionConfigured(String username) {
       return configurationMongoRepository.existsByUsername(username);
    }

}
