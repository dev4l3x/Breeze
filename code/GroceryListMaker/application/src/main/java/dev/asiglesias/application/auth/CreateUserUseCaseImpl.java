package dev.asiglesias.application.auth;

import dev.asiglesias.application.auth.models.UserDataContainer;
import dev.asiglesias.application.auth.repositories.UserRepository;
import dev.asiglesias.application.auth.services.EncryptionService;
import dev.asiglesias.domain.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateUserUseCaseImpl implements CreateUserUseCase {

    private final UserRepository userRepository;

    private final EncryptionService encryptionService;

    @Override
    public void createUser(UserDataContainer userDataContainer) {

        if (userRepository.existsUsername(userDataContainer.getUsername())) {
            throw new RuntimeException();
        }

        User user = new User(userDataContainer.getUsername(), userDataContainer.getPassword());
        String encryptedPassword = encryptionService.encrypt(userDataContainer.getPassword());

        userRepository.createUser(user, encryptedPassword);
    }
}
