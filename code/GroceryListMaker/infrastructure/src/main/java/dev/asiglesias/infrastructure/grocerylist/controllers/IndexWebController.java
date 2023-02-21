package dev.asiglesias.infrastructure.grocerylist.controllers;

import dev.asiglesias.application.auth.services.AuthenticationContext;
import dev.asiglesias.infrastructure.notion.NotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class IndexWebController {

    @Value("${notion.baseuri}")
    private String notionUri;

    private final NotionService notionService;

    private final AuthenticationContext authenticationContext;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("notion_uri", notionUri);

        String username = authenticationContext.getUsername();

        boolean isNotionConfigured = notionService.isNotionConfigured(username);

        model.addAttribute("is_notion_configured", isNotionConfigured);

        return "index";
    }

}
