package dev.asiglesias.application.auth;

import dev.asiglesias.application.auth.models.UserDataContainer;
import dev.asiglesias.application.auth.repositories.UserRepository;
import dev.asiglesias.application.auth.services.EncodingService;
import dev.asiglesias.application.exceptions.InvalidParameterException;
import dev.asiglesias.domain.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateUserUseCaseImpl implements CreateUserUseCase {

    private final UserRepository userRepository;

    private final EncodingService encodingService;

    @Override
    public void createUser(UserDataContainer userDataContainer) {

        if (userDataContainer == null
                ||userDataContainer.getUsername() == null
                || userDataContainer.getUsername().isBlank()
                || userDataContainer.getPassword() == null
                || userDataContainer.getPassword().isBlank()) {
            throw new InvalidParameterException("User must have username and password");
        }

        if (userRepository.existsUsername(userDataContainer.getUsername())) {
            throw new InvalidParameterException("User already exists");
        }

        String encryptedPassword = encodingService.encode(userDataContainer.getPassword());
        User user = new User(userDataContainer.getUsername(), encryptedPassword);

        userRepository.createUser(user);
    }
}
