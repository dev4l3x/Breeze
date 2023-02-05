package dev.asiglesias.application.auth;

import dev.asiglesias.application.auth.models.UserDataContainer;
import dev.asiglesias.application.auth.repositories.UserRepository;
import dev.asiglesias.application.auth.services.EncryptionService;
import dev.asiglesias.application.auth.services.TokenService;
import dev.asiglesias.domain.User;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class SignInUserUseCaseImpl implements SignInUserUseCase {

    private final EncryptionService encryptionService;

    private final UserRepository userRepository;

    private final TokenService tokenService;

    @Override
    public String signIn(UserDataContainer userDataContainer) {
        Optional<User> user = userRepository.findByUsername(userDataContainer.getUsername());

        if (user.isEmpty()) {
            throw new RuntimeException("Username not found");
        }

        String encryptedPassword = encryptionService.encrypt(userDataContainer.getPassword());

        if (!encryptedPassword.equals(user.get().getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return tokenService.createTokenForUser(user.get().getUsername());

    }
}
