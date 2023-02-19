package dev.asiglesias.infrastructure.notion.controllers;

import dev.asiglesias.application.auth.services.EncryptionService;
import dev.asiglesias.infrastructure.notion.controllers.dto.NotionSecretDTO;
import dev.asiglesias.infrastructure.notion.controllers.repositories.NotionConfigurationMongoRepository;
import dev.asiglesias.infrastructure.notion.controllers.repositories.entities.NotionConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("notion")
@RequiredArgsConstructor
public class NotionController {

    final NotionConfigurationMongoRepository notionConfigurationMongoRepository;

    final EncryptionService encryptionService;

    @PostMapping("configuration")
    public ResponseEntity<Void> setupNotionConfiguration(@RequestBody NotionSecretDTO notionSecretDTO) {

        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<NotionConfiguration> configuration = notionConfigurationMongoRepository.findByUsername(username);

        if (configuration.isEmpty()) {
            NotionConfiguration newConfiguration = new NotionConfiguration();
            newConfiguration.setUsername(username);
            configuration = Optional.of(newConfiguration);
        }

        configuration.get().setSecret(encryptionService.encrypt(notionSecretDTO.getSecret()));

        notionConfigurationMongoRepository.save(configuration.get());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
