package dev.asiglesias.infrastructure.grocerylist.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexWebController {

    @Value("${notion.baseuri}")
    private String notionUri;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("notion_uri", notionUri);
        return "index";
    }

}
