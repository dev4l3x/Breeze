package dev.asiglesias.infrastructure.notion.controllers;

import dev.asiglesias.application.auth.services.EncryptionService;
import dev.asiglesias.infrastructure.notion.controllers.dto.NotionSecretDTO;
import dev.asiglesias.infrastructure.notion.controllers.repositories.NotionConfigurationMongoRepository;
import dev.asiglesias.infrastructure.notion.controllers.repositories.entities.NotionConfiguration;
import dev.asiglesias.infrastructure.rest.client.notion.NotionHttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("notion")
@RequiredArgsConstructor
public class NotionController {

    final NotionConfigurationMongoRepository notionConfigurationMongoRepository;

    final EncryptionService encryptionService;

    final NotionHttpClient notionHttpClient;

    @PostMapping("internal-configuration")
    public ResponseEntity<Void> setupInternalNotionConfiguration(@RequestBody NotionSecretDTO notionSecretDTO) {

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

    @PostMapping("configuration")
    public ResponseEntity<Void> setupPublicNotionConfiguration(@RequestParam String code) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String access_token = notionHttpClient.getAccessTokenForCode(code);

        Optional<NotionConfiguration> configuration = notionConfigurationMongoRepository.findByUsername(username);

        if (configuration.isEmpty()) {
            NotionConfiguration newConfiguration = new NotionConfiguration();
            newConfiguration.setUsername(username);
            configuration = Optional.of(newConfiguration);
        }

        configuration.get().setSecret(encryptionService.encrypt(access_token));

        notionConfigurationMongoRepository.save(configuration.get());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
