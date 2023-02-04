package dev.asiglesias.infrastructure.spring.configuration;

import dev.asiglesias.infrastructure.repositories.jpa.UserRepository;
import dev.asiglesias.infrastructure.repositories.jpa.entities.User;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConfiguration {

    final UserRepository repository;

    public DatabaseConfiguration(UserRepository userRepository) {
        this.repository = userRepository;
        loadUsers();
    }

    private void loadUsers() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("test");
        user.setEnabled(true);

        repository.save(user);
    }
}
