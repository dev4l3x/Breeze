package dev.asiglesias.infrastructure.notion.controllers;

import dev.asiglesias.application.auth.services.EncryptionService;
import dev.asiglesias.infrastructure.TechnicalException;
import dev.asiglesias.infrastructure.notion.client.NotionHttpClient;
import dev.asiglesias.infrastructure.notion.client.dto.NotionUserInfo;
import dev.asiglesias.infrastructure.notion.controllers.dto.NotionSecretDTO;
import dev.asiglesias.infrastructure.notion.controllers.repositories.NotionConfigurationMongoRepository;
import dev.asiglesias.infrastructure.notion.controllers.repositories.entities.NotionConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @GetMapping("configuration")
    public void setupPublicNotionConfiguration(@RequestParam String code, HttpServletResponse response) throws IOException {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<NotionUserInfo> notionUserInfo = notionHttpClient.getAccessTokenForCode(code);

        if (notionUserInfo.isEmpty()) {
            throw new TechnicalException("NOTION_NOT_CONFIGURED", "Unable to get token for Notion with provided code");
        }

        Optional<NotionConfiguration> configuration = notionConfigurationMongoRepository.findByUsername(username);

        if (configuration.isEmpty()) {
            NotionConfiguration newConfiguration = new NotionConfiguration();
            newConfiguration.setUsername(username);
            configuration = Optional.of(newConfiguration);
        }

        configuration.get().setSecret(encryptionService.encrypt(notionUserInfo.get().token()));
        configuration.get().setMealPageId(notionUserInfo.get().duplicatedPageId());

        notionConfigurationMongoRepository.save(configuration.get());

        response.sendRedirect("/");
    }

}
