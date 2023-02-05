package dev.asiglesias.infrastructure.auth.repositories.usecases;

import dev.asiglesias.application.auth.repositories.UserRepository;
import dev.asiglesias.domain.User;
import dev.asiglesias.infrastructure.auth.repositories.mongo.UserMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserMongoRepository userMongoRepository;

    @Override
    public User createUser(User user, String password) {
        dev.asiglesias.infrastructure.auth.entitites.User userToCreate =
                new dev.asiglesias.infrastructure.auth.entitites.User();
        userToCreate.setUsername(user.getUsername());
        userToCreate.setPassword(password);
        userToCreate.setEnabled(true);

        userMongoRepository.insert(userToCreate);

        return user;
    }

    @Override
    public boolean existsUsername(String username) {
        return userMongoRepository.existsById(username);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        Optional<dev.asiglesias.infrastructure.auth.entitites.User> user = userMongoRepository.findById(username);
        return user.map(this::mapToDomainUser);
    }

    private User mapToDomainUser(dev.asiglesias.infrastructure.auth.entitites.User user) {
        User mappedUser = new User(user.getUsername(), user.getPassword());
        return mappedUser;
    }
}
