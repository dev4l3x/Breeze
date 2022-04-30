package dev.asiglesias.infrastructure;

import dev.asiglesias.domain.Meal;
import dev.asiglesias.infrastructure.repository.implementation.NotionMealRepository;
import dev.asiglesias.infrastructure.rest.client.notion.NotionHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private NotionMealRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        List<Meal> meals = repository.getMeals();
        System.out.println(meals);
    }
}
