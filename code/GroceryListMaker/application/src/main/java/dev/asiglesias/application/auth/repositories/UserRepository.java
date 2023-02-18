package dev.asiglesias.application.auth.repositories;

import dev.asiglesias.domain.User;

import java.util.Optional;

public interface UserRepository {

    User createUser(User user);

    boolean existsUsername(String username);

    Optional<User> findByUsername(String username);

}
